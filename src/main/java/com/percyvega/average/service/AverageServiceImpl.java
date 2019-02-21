package com.percyvega.average.service;

import com.proto.average.AverageRequest;
import com.proto.average.AverageResponse;
import com.proto.average.AverageServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class AverageServiceImpl extends AverageServiceGrpc.AverageServiceImplBase {

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
}
