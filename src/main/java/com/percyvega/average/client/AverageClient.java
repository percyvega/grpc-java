package com.percyvega.average.client;

import com.proto.average.AverageRequest;
import com.proto.average.AverageResponse;
import com.proto.average.AverageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AverageClient {

    public static void main(String[] args) {
        System.out.println("Starting AverageClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        processClientStream(managedChannel);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void processClientStream(ManagedChannel managedChannel) {
        AverageServiceGrpc.AverageServiceStub averageService =
                AverageServiceGrpc.newStub(managedChannel);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<AverageResponse> responseObserver = new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Received from server: " + value.getAverageResult());
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

        StreamObserver<AverageRequest> requestObserver = averageService.average(responseObserver);

        for (int i = 0; i < 10000; i++) {
            AverageRequest averageRequest = AverageRequest
                    .newBuilder()
                    .setNumeral(i)
                    .build();

            requestObserver.onNext(averageRequest);
        }

        requestObserver.onCompleted();

        try {
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
