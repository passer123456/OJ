package com.onlinejudge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.service.UserService;
import com.onlinejudge.utils.Result;
import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



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

    @GetMapping("/selectPage")
    public Result selectPage(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String role,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    ){
        PageInfo<User> pageInfo = userService.selectPage(keyword, role, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.add(user);
        return Result.success("添加成功");
    }

    @DeleteMapping("/delete/{id}")
	public Result delete(@PathVariable int id)
	{
		userService.delete(id);
		return Result.success("删除成功");
	}

    @PutMapping("update/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }
}
