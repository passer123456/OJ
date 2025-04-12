package com.onlinejudge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlinejudge.mapper.UserMapper;
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
        userMapper.insert(user);
    }

    public void delete(int id) {
        userMapper.delete(id);
    }

    public User selectById(int id) {
        return userMapper.selectById(id);
    }
}
