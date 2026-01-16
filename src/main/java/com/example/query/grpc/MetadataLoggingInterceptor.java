package com.example.query.grpc;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataLoggingInterceptor implements ServerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(MetadataLoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        // 记录所有接收到的metadata
        logger.info("Received gRPC metadata: {}", formatMetadata(headers));
        
        return next.startCall(call, headers);
    }
    
    private String formatMetadata(Metadata metadata) {
        StringBuilder sb = new StringBuilder();
        for (String key : metadata.keys()) {
            if (!key.endsWith("-bin")) { // 避免二进制数据
                Iterable<String> values = metadata.getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
                if (values != null) {
                    sb.append(key).append(": ").append(String.join(", ", values)).append("; ");
                }
            }
        }
        return sb.toString();
    }
}