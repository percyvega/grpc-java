package com.percyvega.greeting.service;

import com.proto.greet.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse
                        .newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(response);
                Thread.sleep(3 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> streamObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                result += "Hello " + value.getGreeting().getFirstName() + ". ";
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                LongGreetResponse longGreetResponse = LongGreetResponse
                        .newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(longGreetResponse);

                responseObserver.onCompleted();
            }
        };

        return streamObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> streamObserver = new StreamObserver<GreetEveryoneRequest>() {

            @Override
            public void onNext(GreetEveryoneRequest value) {
                String logMessage = "Received from client: " + value.getGreeting().getFirstName();
                System.out.println(logMessage);

                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse
                        .newBuilder()
                        .setResult(logMessage)
                        .build();
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return streamObserver;
    }

}
