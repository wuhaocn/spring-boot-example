package com.coral.spring.boot.starter.s2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.coral.spring.boot.starter.s2"})
public class StarterS2Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(StarterS2Application.class);

	public static void main(String[] args) {
		SpringApplication.run(StarterS2Application.class, args);
	}
}
