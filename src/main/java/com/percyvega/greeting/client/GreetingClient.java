package com.percyvega.greeting.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Starting GreetingClient.main()");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetService =
                GreetServiceGrpc.newBlockingStub(managedChannel);

        Greeting greeting = Greeting
                .newBuilder()
                .setFirstName("Percy")
                .setLastName("Vega")
                .build();

        GreetRequest greetRequest = GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse = greetService.greet(greetRequest);
        System.out.println("Greet Response from server: " + greetResponse.getResult());

        System.out.println("Shutting down the channel");
        managedChannel.shutdown();
    }
}
