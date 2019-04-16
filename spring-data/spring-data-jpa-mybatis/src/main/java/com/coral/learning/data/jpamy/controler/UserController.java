package com.coral.learning.data.jpamy.controler;

import com.coral.learning.data.jpamy.entity.User;
import com.coral.learning.data.jpamy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/get")
    @ResponseBody
    public User get(HttpServletRequest request){
        int userId = Integer.parseInt(request.getParameter("id"));
        User user = this.userService.getUserById(userId);
        return user;
    }
    @GetMapping("/mget")
    @ResponseBody
    public User mget(HttpServletRequest request){
        String userStr = request.getParameter("user");
        User user = this.userService.getUserByUser(userStr);
        return user;
    }

}