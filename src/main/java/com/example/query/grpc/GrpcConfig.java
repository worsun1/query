package com.example.query.grpc;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @GrpcGlobalServerInterceptor
    public MetadataLoggingInterceptor metadataLoggingInterceptor() {
        return new MetadataLoggingInterceptor();
    }
}