package com.spring.springcloud.feign.service;

import com.alibaba.fastjson.JSONObject;
import com.spring.springcloud.feign.entity.AuthEntity;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthService {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestMapping(value = "/app/mock/auth/check", method = RequestMethod.POST)
    AuthEntity auth(@RequestBody AuthEntity AuthEntity);
}
