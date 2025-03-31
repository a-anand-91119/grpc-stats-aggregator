package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
//@Component
//@GlobalServerInterceptor
public class GrpcLoggingInterceptor implements ServerInterceptor {

    private static final String REQUEST_ID_KEY = "REQUEST_ID";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler
    ) {
        String requestId = metadata.get(Metadata.Key.of(REQUEST_ID_KEY, Metadata.ASCII_STRING_MARSHALLER));
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        try (MDC.MDCCloseable ignored = MDC.putCloseable(REQUEST_ID_KEY, requestId)) {
            log.info(
                    "gRPC Request started: method={}, requestId={}",
                    serverCall.getMethodDescriptor().getFullMethodName(),
                    requestId
            );

            // Proceed with the call and wrap the listener to log completion or cancellation
            ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
            String finalRequestId = requestId;
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
                @Override
                public void onComplete() {
                    log.info(
                            "gRPC Request completed: method={}, requestId={}",
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            finalRequestId
                    );
                    super.onComplete();
                }

                @Override
                public void onCancel() {
                    log.info(
                            "gRPC Request canceled: method={}, requestId={}",
                            serverCall.getMethodDescriptor().getFullMethodName(),
                            finalRequestId
                    );
                    super.onCancel();
                }
            };
        }
    }
}
