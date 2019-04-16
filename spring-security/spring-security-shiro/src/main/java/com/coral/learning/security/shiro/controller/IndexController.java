package com.coral.learning.security.shiro.controller;

import com.coral.learning.security.shiro.entity.User;
import com.coral.learning.security.shiro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()){
            return "index";
        }
        return "login";
    }
    @GetMapping("/loginsub")
    public String loginSub(HttpServletRequest request){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            //shiro login
            if (shiroLogin(username, password)){
                return "index";
            }
        } catch (AuthenticationException ae) {
            ae.printStackTrace();
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "login";
    }

    /**
     * shiro登录
     *
     * @param username
     * @param password
     * @return
     */
    private boolean shiroLogin(String username, String password){
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //token.setRememberMe(rm);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        if (subject.isAuthenticated()) {
            User user = (User) subject.getPrincipal();
            Session session = subject.getSession();
            session.setAttribute("session", user);
            session.setAttribute("username", username);
            return true;
        }


        return false;
    }

}