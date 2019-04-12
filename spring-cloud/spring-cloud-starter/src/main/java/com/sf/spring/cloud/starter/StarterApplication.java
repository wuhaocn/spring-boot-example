package com.sf.spring.cloud.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@SpringBootApplication
public class StarterApplication {
	@RequestMapping("/doIndex")
	public String doIndex() {
		return "doIndex";
	}
	public static void main(String[] args) {
		SpringApplication.run(StarterApplication.class, args);
	}
}
