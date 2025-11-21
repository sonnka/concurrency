package com.project.concurrency.grpc.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcGlobalServerInterceptor
public class LoggingInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    private static final Metadata.Key<String> USER_ID_HEADER =
            Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String userId = metadata.get(USER_ID_HEADER);

        RequestContext.setUserId(userId);

        log.info("gRPC IN: method={}, userId={}",
                serverCall.getMethodDescriptor().getFullMethodName(),
                userId);

        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);

        return new ContextCleaningServerCallListener<>(listener, userId);
    }

    private static class ContextCleaningServerCallListener<ReqT> extends
            ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

        private final String userId;

        public ContextCleaningServerCallListener(ServerCall.Listener<ReqT> delegate, String userId) {
            super(delegate);
            this.userId = userId;
        }

        @Override
        public void onMessage(ReqT message) {
            RequestContext.setUserId(userId);
            try {
                super.onMessage(message);
            } catch (Exception ignored) {

            }
        }

        @Override
        public void onHalfClose() {
            RequestContext.setUserId(userId);
            try {
                super.onHalfClose();
            } catch (Exception ignored) {

            }
        }

        @Override
        public void onComplete() {
            try {
                super.onComplete();
            } finally {
                RequestContext.clear();
            }
        }

        @Override
        public void onCancel() {
            try {
                super.onCancel();
            } finally {
                RequestContext.clear();
            }
        }
    }
}
