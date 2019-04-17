package com.coral.learning.security.starter.controller;

import com.coral.learning.security.starter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("")
public class IndexController {
    @Resource
    private UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/index")
    public String index(){
        return "index";
    }


}