package com.coral.spring.boot.starter.multi;

import com.coral.spring.boot.starter.s1.StarterS1Application;
import com.coral.spring.boot.starter.s2.StarterS2Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class StarterMultiApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(StarterMultiApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(StarterS1Application.class, mergeArgs("--spring.profiles.active=s1", args));

        SpringApplication.run(StarterS2Application.class, mergeArgs("--spring.profiles.active=s2", args));
    }

    public static String[] mergeArgs(String arg, String[] args) {
        String[] retArgs = new String[args.length + 1];
        retArgs[0] = arg;
        for (int i = 1; i < retArgs.length; i++) {
            retArgs[i] = args[i - 1];
        }
        return retArgs;
    }
}
