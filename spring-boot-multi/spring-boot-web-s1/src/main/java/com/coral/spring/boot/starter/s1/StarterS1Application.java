package com.coral.spring.boot.starter.s1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.coral.spring.boot.starter.s1")
public class StarterS1Application {
    public static void main(String[] args) {

        SpringApplication.run(StarterS1Application.class, args);
    }
}
