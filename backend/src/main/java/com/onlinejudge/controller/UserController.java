package com.onlinejudge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.service.UserService;
import com.onlinejudge.utils.LoginInfo;
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
@RequestMapping("/api/user")
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

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids)
    {
        for (int id : ids) {
            userService.delete(id);
        }
        return Result.success("批量删除成功");
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);     
        return Result.success("更新成功");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        LoginInfo loginInfo=userService.login(user);    
        return Result.success(loginInfo);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            // 捕获用户名已存在的异常
            return Result.error(400, e.getMessage()); 
        } catch (Exception e) {
            // 其他错误（如数据库问题）
            return Result.error(500, "注册失败: " + e.getMessage());
        }
    }
}
