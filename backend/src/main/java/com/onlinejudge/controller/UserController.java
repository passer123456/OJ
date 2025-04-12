package com.onlinejudge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.service.UserService;
import com.onlinejudge.utils.Result;
import com.onlinejudge.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
	UserService userService;
   
    @GetMapping("/selectAll")
    public Result selectAllUser() {
        List<User> users = userService.selectAllUsers();
        return Result.success(users);
    }
    
}
