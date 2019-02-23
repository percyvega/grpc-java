package com.percyvega.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Starting CalculatorClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

//        processClientStream(managedChannel);
        processErrorCall(managedChannel);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void processErrorCall(ManagedChannel managedChannel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorService =
                CalculatorServiceGrpc.newBlockingStub(managedChannel);

        int number = -1;
        try {
            SquareRootResponse squareRootResponse = calculatorService.squareRoot(SquareRootRequest.newBuilder()
                    .setNumber(number)
                    .build());

            System.out.println("Square root of " + number + " is " + squareRootResponse.getNumberRoot());
        } catch (StatusRuntimeException e) {
            System.out.println("Got an exception when calling square root.");
            e.printStackTrace();
        }
    }

    private static void processClientStream(ManagedChannel managedChannel) {
        CalculatorServiceGrpc.CalculatorServiceStub calculatorService =
                CalculatorServiceGrpc.newStub(managedChannel);

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

        StreamObserver<AverageRequest> requestObserver = calculatorService.average(responseObserver);

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
