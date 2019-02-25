package com.percyvega.calculator.server;

import com.percyvega.calculator.service.CalculatorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting CalculatorServer.main()");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new CalculatorServiceImpl())
                .addService(ProtoReflectionService.newInstance()) // for reflection
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
