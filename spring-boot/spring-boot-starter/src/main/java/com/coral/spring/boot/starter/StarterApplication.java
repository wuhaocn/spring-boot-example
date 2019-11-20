package com.coral.spring.boot.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class StarterApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(StarterApplication.class);

	@RequestMapping("/test")
	public String doIndex() {

		LOGGER.error("doIndex", "test");
		return "test";
	}
	public static void main(String[] args) {
		SpringApplication.run(StarterApplication.class, args);
	}
}
