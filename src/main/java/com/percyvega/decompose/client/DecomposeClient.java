package com.percyvega.decompose.client;

import com.proto.decompose.DecomposeRequest;
import com.proto.decompose.DecomposeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DecomposeClient {

    public static void main(String[] args) {
        System.out.println("Starting DecomposeClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        DecomposeServiceGrpc.DecomposeServiceBlockingStub decomposeService =
                DecomposeServiceGrpc.newBlockingStub(managedChannel);

        processDecomposeRequest(decomposeService);

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }

    private static void processDecomposeRequest(DecomposeServiceGrpc.DecomposeServiceBlockingStub decomposeService) {
        DecomposeRequest decomposeRequest = DecomposeRequest
                .newBuilder()
                .setNumberToDecompose(567890304)
                .build();

        decomposeService
                .decompose(decomposeRequest)
                .forEachRemaining(decomposeResponse -> System.out.println(decomposeResponse.getFactor()));
    }
}