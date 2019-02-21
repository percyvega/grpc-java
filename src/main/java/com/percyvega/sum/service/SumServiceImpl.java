package com.percyvega.sum.service;

import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.stub.StreamObserver;

public class SumServiceImpl extends SumServiceGrpc.SumServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setTotal(request.getAddend1() + request.getAddend2())
                .build();

        responseObserver.onNext(sumResponse);

        responseObserver.onCompleted();
    }
}
