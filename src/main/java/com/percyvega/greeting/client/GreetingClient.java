package com.percyvega.greeting.client;

import com.proto.greet.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) throws SSLException {
        System.out.println("Starting GreetingClient.main()");

//        ManagedChannel managedChannel = ManagedChannelBuilder
//                .forAddress("localhost", 50051)
//                .usePlaintext()
//                .build();

        ManagedChannel managedChannel = NettyChannelBuilder // for SSL
                .forAddress("localhost", 50051)
                .sslContext(GrpcSslContexts
                        .forClient()
                        .trustManager(new File("ssl/ca.crt"))
                    .build())
                .build();

//        processWithDeadline(managedChannel);

        processUnary(managedChannel);
//        processServerStream(managedChannel);
//        processClientStream(managedChannel);
//        processBidirectionalStream(managedChannel);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void processUnary(ManagedChannel managedChannel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetService =
                GreetServiceGrpc.newBlockingStub(managedChannel);

        Greeting greeting = Greeting
                .newBuilder()
                .setFirstName("Percy")
                .setLastName("Vega")
                .build();

        GreetRequest greetRequest = GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse =
                greetService.greet(greetRequest);
        System.out.println("Greet Response from server: " + greetResponse.getResult());
    }

    private static void processWithDeadline(ManagedChannel managedChannel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetService =
                GreetServiceGrpc.newBlockingStub(managedChannel);

        Greeting greeting = Greeting
                .newBuilder()
                .setFirstName("Percy")
                .setLastName("Vega")
                .build();

        GreetRequest greetRequest = GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        try {
            GreetResponse greetResponse =
                    greetService
                            .withDeadline(Deadline.after(800, TimeUnit.MILLISECONDS))
                            .greetWithDeadline(greetRequest);
            System.out.println("Greet Response from server: " + greetResponse.getResult());
        } catch (StatusRuntimeException e) {
            if(e.getStatus().getCode().equals(Status.DEADLINE_EXCEEDED.getCode())) {
                System.out.println("Deadline has been exceeded, we won't want the response anymore");
                e.printStackTrace();
            }
        }
    }

    private static void processServerStream(ManagedChannel managedChannel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetService =
                GreetServiceGrpc.newBlockingStub(managedChannel);

        Greeting greeting = Greeting
                .newBuilder()
                .setFirstName("Percy")
                .setLastName("Vega")
                .build();

        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        greetService
                .greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> System.out.println("Greet Response from server: " + greetManyTimesResponse.getResult()));
    }

    private static void processClientStream(ManagedChannel managedChannel) {
        GreetServiceGrpc.GreetServiceStub greetService =
                GreetServiceGrpc.newStub(managedChannel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<LongGreetResponse> responseObserver = new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("Received from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something");

                countDownLatch.countDown();
            }
        };

        StreamObserver<LongGreetRequest> requestObserver = greetService.longGreet(responseObserver);

        for (int i = 0; i < 10; i++) {
            Greeting greeting = Greeting
                    .newBuilder()
                    .setFirstName("Percy" + i)
                    .setLastName("Vega" + i)
                    .build();

            LongGreetRequest longGreetRequest = LongGreetRequest
                    .newBuilder()
                    .setGreeting(greeting)
                    .build();

            requestObserver.onNext(longGreetRequest);
            System.out.println("Sent to server: " + longGreetRequest.getGreeting());
        }

        requestObserver.onCompleted();

        try {
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void processBidirectionalStream(ManagedChannel managedChannel) {
        GreetServiceGrpc.GreetServiceStub greetService =
                GreetServiceGrpc.newStub(managedChannel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneResponse> responseObserver = new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Received from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something");

                countDownLatch.countDown();
            }
        };

        StreamObserver<GreetEveryoneRequest> requestObserver = greetService.greetEveryone(responseObserver);

        for (int i = 0; i < 10; i++) {
            Greeting greeting = Greeting
                    .newBuilder()
                    .setFirstName("Percy" + i)
                    .setLastName("Vega" + i)
                    .build();

            GreetEveryoneRequest greetEveryoneRequest = GreetEveryoneRequest
                    .newBuilder()
                    .setGreeting(greeting)
                    .build();

            requestObserver.onNext(greetEveryoneRequest);
            System.out.println("Sent to server: " + greetEveryoneRequest.getGreeting());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        requestObserver.onCompleted();

        try {
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
