package com.percyvega.maximum.server;

import com.percyvega.maximum.service.FindMaximumServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MaximumServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting MaximumServer.main()");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new FindMaximumServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
