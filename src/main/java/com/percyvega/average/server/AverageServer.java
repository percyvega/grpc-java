package com.percyvega.average.server;

import com.percyvega.average.service.AverageServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class AverageServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting AverageServer.main()");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new AverageServiceImpl())
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
