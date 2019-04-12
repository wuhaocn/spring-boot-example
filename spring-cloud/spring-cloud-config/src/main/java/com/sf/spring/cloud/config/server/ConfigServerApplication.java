package com.sf.spring.cloud.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    @RequestMapping("/")
    public String home() {
        return "configserver index";
    }
    @RequestMapping("/info")
    public String info() {
        return "configserver";
    }
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ConfigServerApplication.class.getClassLoader().getResourceAsStream("application-configserver.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ConfigServerApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}