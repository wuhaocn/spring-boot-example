package com.sf.spring.cloud.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableAutoConfiguration
public class HystrixApplication {

    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = HystrixApplication.class.getClassLoader().getResourceAsStream("application-hystrix.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(HystrixApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }

}
