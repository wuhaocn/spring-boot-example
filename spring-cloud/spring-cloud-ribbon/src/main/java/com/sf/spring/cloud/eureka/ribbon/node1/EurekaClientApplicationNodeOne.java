package com.sf.spring.cloud.eureka.ribbon.node1;

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
public class EurekaClientApplicationNodeOne {
    @RequestMapping("/")
    public String home() {
        return "home page";
    }
    @RequestMapping("/info")
    public String info() {
        return "info page one";
    }
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = EurekaClientApplicationNodeOne.class.getClassLoader().getResourceAsStream("application-client-nodeone.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(EurekaClientApplicationNodeOne.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}