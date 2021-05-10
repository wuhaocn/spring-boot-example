package com.coral.spring.boot.starter.s1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S1Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(S1Controller.class);

	@RequestMapping("/test")
	public String doIndex() {
		LOGGER.error("doIndex", "test");
		return "S1Controller";
	}
	@RequestMapping("/test1")
	public String doIndex1() {
		LOGGER.error("doIndex", "test");
		return "S1Controller";
	}
}
