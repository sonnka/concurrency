package com.project.concurrency.config;

import io.grpc.ServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GrpcVirtualThreadsConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService grpcVirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public GrpcServerConfigurer grpcServerExecutorConfigurer(ExecutorService grpcVirtualThreadExecutor) {
        return (ServerBuilder<?> serverBuilder) -> serverBuilder.executor(grpcVirtualThreadExecutor);
    }
}
