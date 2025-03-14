package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.services;

import com.google.protobuf.Empty;
import dev.notyouraverage.project.one.proto.GetNameStatsRequest;
import dev.notyouraverage.project.one.proto.GetNameStatsResponse;
import dev.notyouraverage.project.one.proto.NameServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
public class HelloService extends NameServiceGrpc.NameServiceImplBase {
    @Override
    public void getNameStats(GetNameStatsRequest request, StreamObserver<GetNameStatsResponse> responseObserver) {
        if (request.getName().startsWith("error")) {
            throw new IllegalArgumentException("Bad name: " + request.getName());
        }
        if (request.getName().startsWith("internal")) {
            throw new RuntimeException();
        }
        GetNameStatsResponse getNameStatsResponse = GetNameStatsResponse.newBuilder()
                .setName(request.getName())
                .setCount(200)
                .build();
        responseObserver.onNext(getNameStatsResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllNameStats(Empty request, StreamObserver<GetNameStatsResponse> responseObserver) {
        int count = 0;
        while (count < 100) {
            GetNameStatsResponse getNameStatsResponse = GetNameStatsResponse.newBuilder()
                    .setName(UUID.randomUUID().toString())
                    .setCount(count)
                    .build();
            responseObserver.onNext(getNameStatsResponse);
            count++;
        }
        responseObserver.onCompleted();
    }
}
