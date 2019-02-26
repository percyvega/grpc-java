package com.percyvega.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.LocalDateTime;
import java.util.Random;

public class BlogClient {

    public static void main(String[] args) {
        System.out.println("Starting BlogClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

//        createBlog(managedChannel);
//        readBlog(managedChannel);
//        updateBlog(managedChannel);
//        deleteBlog(managedChannel);

        listBlogs(managedChannel);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void listBlogs(ManagedChannel managedChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blockingStub =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        blockingStub
                .listBlogs(EmptyRequest.newBuilder().build())
                .forEachRemaining(
                    blogResponse -> System.out.println(blogResponse.getBlog())
                );
    }

    private static void deleteBlog(ManagedChannel managedChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blockingStub =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        String blogId = "5c74d21d74ba1b2718630161";

        BlogResponse blogResponse = blockingStub.deleteBlog(BlogIdRequest
                .newBuilder()
                .setBlogId(blogId)
                .buildPartial());

        System.out.println("Response from server: " + blogResponse);
    }

    private static void updateBlog(ManagedChannel managedChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blockingStub =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        Blog blog = Blog
                .newBuilder()
                .setId("5c74d22774ba1b2718630163")
                .setAuthorId("Updated Percy " + new Random().nextInt())
                .setTitle("Updated Title " + LocalDateTime.now().toString())
                .setContent("Updated Content " + new Random().nextLong())
                .build();

        BlogRequest blogRequest = BlogRequest
                .newBuilder()
                .setBlog(blog)
                .build();
        BlogResponse blogResponse = blockingStub.updateBlog(blogRequest);

        System.out.println("Response from server: " + blogResponse);
    }

    private static void readBlog(ManagedChannel managedChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blockingStub =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        String blogId = "5c74d22474ba1b2718630162";

        BlogResponse blogResponse = blockingStub.readBlog(BlogIdRequest
                .newBuilder()
                .setBlogId(blogId)
                .buildPartial());

        System.out.println("Response from server: " + blogResponse);
    }

    private static void createBlog(ManagedChannel managedChannel) {
        BlogServiceGrpc.BlogServiceBlockingStub blockingStub =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        Blog blog = Blog
                .newBuilder()
                .setAuthorId("Percy " + new Random().nextInt())
                .setTitle("Title " + LocalDateTime.now().toString())
                .setContent("Content " + new Random().nextLong())
                .build();

        BlogRequest blogRequest = BlogRequest
                .newBuilder()
                .setBlog(blog)
                .build();

        BlogResponse blogResponse = blockingStub.createBlog(blogRequest);
        System.out.println("Response from server: " + blogResponse.toString());
    }
}
