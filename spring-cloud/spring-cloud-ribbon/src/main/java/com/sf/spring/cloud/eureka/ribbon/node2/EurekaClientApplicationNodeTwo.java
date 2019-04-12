package com.sf.spring.cloud.eureka.ribbon.node2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaClientApplicationNodeTwo {
    @RequestMapping("/")
    public String home() {
        return "home page";
    }
    @RequestMapping("/info")
    public String info() {
        return "info page two";
    }
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = EurekaClientApplicationNodeTwo.class.getClassLoader().getResourceAsStream("application-client-nodetwo.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(EurekaClientApplicationNodeTwo.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}