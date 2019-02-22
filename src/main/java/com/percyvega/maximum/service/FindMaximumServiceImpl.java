package com.percyvega.maximum.service;

import com.proto.maximum.FindMaximumRequest;
import com.proto.maximum.FindMaximumResponse;
import com.proto.maximum.FindMaximumServiceGrpc;
import io.grpc.stub.StreamObserver;

public class FindMaximumServiceImpl extends FindMaximumServiceGrpc.FindMaximumServiceImplBase {
    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {
        StreamObserver<FindMaximumRequest> streamObserver = new StreamObserver<FindMaximumRequest>() {

            int maxSoFar = 0;

            @Override
            public void onNext(FindMaximumRequest value) {
                int potentialMaximum = value.getPotentialMaximum();
                String logMessage = "Received from client: " + potentialMaximum;
                System.out.println(logMessage);

                if (potentialMaximum > maxSoFar) {
                    maxSoFar = potentialMaximum;

                    FindMaximumResponse findMaximumResponse = FindMaximumResponse
                            .newBuilder()
                            .setActualMaximum(maxSoFar)
                            .build();
                    responseObserver.onNext(findMaximumResponse);
                }
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
