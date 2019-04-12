package com.sf.spring.cloud.eureka.ribbon.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = EurekaServerApplication.class.getClassLoader().getResourceAsStream("application-server.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(EurekaServerApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}