package com.spring.springcloud.feign.controller;

import com.alibaba.fastjson.JSONObject;
import com.spring.springcloud.feign.entity.AuthEntity;
import com.spring.springcloud.feign.service.AuthService;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public void check(){
        AuthService service = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3))
                .target(AuthService.class, "http://127.0.0.1:8085");

        AuthEntity authEntity = new AuthEntity();
        authEntity.setReason("123456");
        System.out.println(JSONObject.toJSONString(authEntity));
        boolean pass = false;
    }
}
