package com.sf.spring.cloud.zipkin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import zipkin.server.EnableZipkinServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@SpringBootApplication
@EnableZipkinServer
public class ZipKinServerApplication {
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ZipKinServerApplication.class.getClassLoader().getResourceAsStream("application-zipkin-server.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ZipKinServerApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}