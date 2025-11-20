package com.project.concurrency.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadsConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService tomcatVirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatVirtualThreadCustomizer(
            ExecutorService tomcatVirtualThreadExecutor) {
        return factory -> factory.addConnectorCustomizers(connector ->
                connector.getProtocolHandler().setExecutor(tomcatVirtualThreadExecutor)
        );
    }
}