package com.project.concurrency.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatVirtualThreadExecutor() {
        return factory -> factory.addConnectorCustomizers(this::configureConnector);
    }

    private void configureConnector(Connector connector) {
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        protocolHandler.setExecutor(executor);
    }
}