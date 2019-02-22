package com.percyvega.maximum.client;

import com.proto.maximum.FindMaximumRequest;
import com.proto.maximum.FindMaximumResponse;
import com.proto.maximum.FindMaximumServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FindMaximumClient {

    public static void main(String[] args) {
        System.out.println("Starting FindMaximumClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        processBidirectionalStream(managedChannel);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void processBidirectionalStream(ManagedChannel managedChannel) {
        FindMaximumServiceGrpc.FindMaximumServiceStub findMaximumService =
                FindMaximumServiceGrpc.newStub(managedChannel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<FindMaximumResponse> responseObserver = new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("Received from server: " + value.getActualMaximum());
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

        StreamObserver<FindMaximumRequest> requestObserver = findMaximumService.findMaximum(responseObserver);

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            FindMaximumRequest findMaximumRequest = FindMaximumRequest
                    .newBuilder()
                    .setPotentialMaximum(Math.abs(random.nextInt() % 100))
                    .build();

            requestObserver.onNext(findMaximumRequest);
            System.out.println("Sent to server: " + findMaximumRequest.getPotentialMaximum());

            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        requestObserver.onCompleted();

        try {
            countDownLatch.await(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
