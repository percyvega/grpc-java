package com.percyvega.sum.client;

import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SumClient {

    public static void main(String[] args) {
        System.out.println("Starting SumClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        SumServiceGrpc.SumServiceBlockingStub sumService =
                SumServiceGrpc.newBlockingStub(managedChannel);

        SumRequest sumRequest = SumRequest
                .newBuilder()
                .setAddend1(3)
                .setAddend2(10)
                .build();

        SumResponse sumResponse = sumService.sum(sumRequest);
        System.out.println("Sum Response from server: " + sumResponse.getTotal());

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }
}
