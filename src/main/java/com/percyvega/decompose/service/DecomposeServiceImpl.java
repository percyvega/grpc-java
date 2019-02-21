package com.percyvega.decompose.service;

import com.proto.decompose.DecomposeRequest;
import com.proto.decompose.DecomposeResponse;
import com.proto.decompose.DecomposeServiceGrpc;
import io.grpc.stub.StreamObserver;

public class DecomposeServiceImpl extends DecomposeServiceGrpc.DecomposeServiceImplBase {

    @Override
    public void decompose(DecomposeRequest request, StreamObserver<DecomposeResponse> responseObserver) {
        int numberToDecompose = request.getNumberToDecompose();

        int factor = 2;
        while (factor <= numberToDecompose) {
            if(numberToDecompose % factor == 0) {
                numberToDecompose = numberToDecompose / factor;

                //send factor to client
                DecomposeResponse decomposeResponse = DecomposeResponse
                        .newBuilder()
                        .setFactor(factor)
                        .build();

                responseObserver.onNext(decomposeResponse);
            } else {
                factor++;
            }
        }

        responseObserver.onCompleted();
    }
}
