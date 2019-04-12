package com.sf.spring.cloud.zipkin.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@SpringBootApplication
@RestController
public class ZipKinInvokingApplication {
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping("/send")
    public String send(){
        return restTemplate.getForObject("http://localhost:8191/send", String.class);
    }
    @RequestMapping("/receiver")
    public String receiver(){
        return restTemplate.getForObject("http://localhost:8191/receiver", String.class);

    }

    @Bean
    public AlwaysSampler defaultSampler(){
        return new AlwaysSampler();
    }

    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ZipKinInvokingApplication.class.getClassLoader().getResourceAsStream("application-zipkin-invoking.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ZipKinInvokingApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}