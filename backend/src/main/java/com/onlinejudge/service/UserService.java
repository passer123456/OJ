package com.onlinejudge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlinejudge.mapper.UserMapper;
import com.onlinejudge.entity.User;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> selectAllUsers() {
        return userMapper.selectAllUsers();
    }
}
