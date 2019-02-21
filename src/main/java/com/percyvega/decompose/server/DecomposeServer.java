package com.percyvega.decompose.server;

import com.percyvega.decompose.service.DecomposeServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class DecomposeServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting GreetingServer.main()");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new DecomposeServiceImpl())
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
