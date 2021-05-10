package com.coral.spring.boot.starter.s2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S2Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(S2Controller.class);

	@RequestMapping("/test")
	public String doIndex() {
		LOGGER.error("doIndex", "test");
		return "S2Controller";
	}

	@RequestMapping("/test2")
	public String doIndex2() {
		LOGGER.error("doIndex", "test");
		return "S2Controller";
	}

}
