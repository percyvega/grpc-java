package com.percyvega.calculator.service;

import com.proto.calculator.AverageRequest;
import com.proto.calculator.AverageResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> streamObserver = new StreamObserver<AverageRequest>() {

            List<Integer> integerList = new ArrayList<>();

            @Override
            public void onNext(AverageRequest value) {
                integerList.add(value.getNumeral());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double averageResult = 0;

                OptionalDouble optionalDouble = integerList.stream().mapToDouble(value -> value).average();
                if(optionalDouble.isPresent()) {
                    averageResult = optionalDouble.getAsDouble();
                }

                AverageResponse averageResponse = AverageResponse
                        .newBuilder()
                        .setAverageResult(averageResult)
                        .build();

                responseObserver.onNext(averageResponse);

                responseObserver.onCompleted();
            }
        };

        return streamObserver;
    }

    @Override
    public void squareRoot(com.proto.calculator.SquareRootRequest request, StreamObserver<com.proto.calculator.SquareRootResponse> responseObserver) {
        int number = request.getNumber();

        if (number >= 0) {
            double sqrt = Math.sqrt(number);

            SquareRootResponse response = SquareRootResponse
                    .newBuilder()
                    .setNumberRoot(sqrt)
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onError(Status
                    .INVALID_ARGUMENT
                    .withDescription("The number sent must be positive.")
                    .augmentDescription("Number sent: " + number)
                    .asRuntimeException());
        }
    }
}
