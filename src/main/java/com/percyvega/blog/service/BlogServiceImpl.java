package com.percyvega.blog.service;

import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import com.proto.blog.*;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase mongoDatabase = mongoClient.getDatabase("VertxBlog");
    private MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("blog");

    // will ignore the blog id
    @Override
    public void createBlog(BlogRequest request, StreamObserver<BlogResponse> responseObserver) {
        System.out.println("Starting BlogServiceImpl.createBlog");

        Blog blog = request.getBlog();

        Document document = blogToDocumentIgnoreID(blog);

        mongoCollection.insertOne(document);

        String id = document.getObjectId("_id").toString();
        System.out.println("Inserted blog ID: " + id);

        BlogResponse blogResponse = BlogResponse
                .newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .buildPartial();
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();

        System.out.println("Finishing BlogServiceImpl.createBlog");
    }

    @Override
    public void readBlog(BlogIdRequest request, StreamObserver<BlogResponse> responseObserver) {
        System.out.println("Starting BlogServiceImpl.readBlog");

        String blogId = request.getBlogId();
        ObjectId objectId = new ObjectId(blogId);

        Document document = mongoCollection.find(eq("_id", objectId)).first();
        Blog blog = documentToBlog(document);

        BlogResponse blogResponse = BlogResponse.newBuilder().setBlog(blog).build();
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();

        System.out.println("Finishing BlogServiceImpl.readBlog");
    }

    @Override
    public void updateBlog(BlogRequest request, StreamObserver<BlogResponse> responseObserver) {
        System.out.println("Starting BlogServiceImpl.updateBlog");

        Blog blog = request.getBlog();

        Document document = blogToDocumentIgnoreID(blog);

        String blogId = blog.getId();
        ObjectId objectId = new ObjectId(blogId);

        mongoCollection.replaceOne(eq("_id", objectId), document);

        Document updatedDocument = mongoCollection.find(eq("_id", objectId)).first();

        BlogResponse blogResponse = BlogResponse
                .newBuilder()
                .setBlog(documentToBlog(updatedDocument))
                .buildPartial();
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();

        System.out.println("Finishing BlogServiceImpl.updateBlog");
    }

    @Override
    public void deleteBlog(BlogIdRequest request, StreamObserver<BlogResponse> responseObserver) {
        System.out.println("Starting BlogServiceImpl.deleteBlog");

        String blogId = request.getBlogId();
        ObjectId objectId = new ObjectId(blogId);

        Document documentToDelete = mongoCollection.find(eq("_id", objectId)).first();

        mongoCollection.deleteOne(eq("_id", objectId));

        BlogResponse blogResponse = BlogResponse
                .newBuilder()
                .setBlog(documentToBlog(documentToDelete))
                .buildPartial();
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();

        System.out.println("Finishing BlogServiceImpl.deleteBlog");
    }

    @Override
    public void listBlogs(EmptyRequest request, StreamObserver<BlogResponse> responseObserver) {
        System.out.println("Starting BlogServiceImpl.listBlogs");

        mongoCollection
                .find()
                .iterator()
                .forEachRemaining(document -> responseObserver
                        .onNext(BlogResponse
                                .newBuilder()
                                .setBlog(documentToBlog(document))
                        .build()));

        responseObserver.onCompleted();

        System.out.println("Finishing BlogServiceImpl.listBlogs");
    }

    private Document blogToDocumentIgnoreID(Blog blog) {
        return new Document()
                .append("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());
    }

    private Blog documentToBlog(Document document) {
        Blog blog = Blog
                .newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .build();

        return blog;
    }
}
