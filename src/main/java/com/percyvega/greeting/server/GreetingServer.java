package com.percyvega.greeting.server;

import com.percyvega.greeting.service.GreetServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting GreetingServer.main()");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetServiceImpl())
                .useTransportSecurity( // for SSL
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem"))
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
