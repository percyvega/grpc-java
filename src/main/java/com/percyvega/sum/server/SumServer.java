package com.percyvega.sum.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SumServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting SumServer.main()");

        Server server = ServerBuilder
                .forPort(50052)
                .addService(new SumServiceImpl())
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
