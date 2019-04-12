package com.sf.spring.cloud.eureka.ribbon.ribbon;

import com.sf.spring.cloud.eureka.ribbon.server.EurekaServerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class RibbonApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        return restTemplate.getForObject("http://eureka.node2/info/", String.class);
    }
    public static void main(String[] args) throws IOException {
        SpringApplication springApplication = new SpringApplication(RibbonApplication.class);
        Properties configProperties = new Properties();
        InputStream config = EurekaServerApplication.class.getClassLoader().getResourceAsStream("application-ribbon-client.properties");
        configProperties.load(config);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }


}