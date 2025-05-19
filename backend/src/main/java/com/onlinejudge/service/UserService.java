package com.onlinejudge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlinejudge.mapper.UserMapper;
import com.onlinejudge.utils.LoginInfo;
import com.onlinejudge.utils.TokenUtil;
import com.onlinejudge.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.User;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> selectAllUsers() {
        return userMapper.selectAllUsers();
    }

    public PageInfo<User> selectPage(String keyword,String role,Integer page, Integer size) {
        PageHelper.startPage(page, size);
        
        List<User> list = userMapper.selectUserList(keyword,role);
        
        return PageInfo.of(list);
    }

    public void add(User user) {
        if(userMapper.selectByUsername(user.getUsername()) != null){
            throw new RuntimeException("用户名存在");
        }
        userMapper.insert(user);
    }

    public void delete(int id) {
        userMapper.delete(id);
    }

    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    public void update(User user) {
        userMapper.updateById(user);
    }

    public LoginInfo login(User user) {
        User dbUser=userMapper.selectByUsername(user.getUsername());
        LoginInfo loginInfo = new LoginInfo();
        if(dbUser==null){
            throw new CustomException("用户不存在");
        }
        if(!dbUser.getPassword().equals(user.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        // 生成token
        loginInfo.setUserId(dbUser.getUserId());
        loginInfo.setUsername(dbUser.getUsername());
        loginInfo.setRole(dbUser.getRole());
        loginInfo.setAvatar(dbUser.getAvatar());
        loginInfo.setToken(TokenUtil.createToken(dbUser.getUserId() + "-" + dbUser.getRole()));
        return loginInfo;
    }

    public void register(User user) {
        add(user);
    }

    public void changePassword(Integer userId,String oldPassword ,String newPassword) {
        User user = userMapper.selectById(userId);
        if(user.getPassword() == null || !user.getPassword().equals(oldPassword)){
            throw new CustomException("旧密码错误");
        }
        if(user.getPassword().equals(newPassword)){
            throw new CustomException("新密码不能与旧密码相同");
        }
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }
}
