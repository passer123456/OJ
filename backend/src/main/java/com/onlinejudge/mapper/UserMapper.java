package com.onlinejudge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.onlinejudge.entity.User;

@Mapper
public interface UserMapper 
{
    // 查询所有用户
    List<User> selectAllUsers();

    // 分页查询列表
    List<User> selectUserList( 
        @Param("keyword") String keyword,
        @Param("role") String role
    );
    
    // 插入用户
    void insert(User user);

    // 删除用户
    void delete(int id);

    // 根据ID查询用户
    User selectById(int id);

    // 根据ID更新用户
    void updateById(User user);

    // 根据用户名查询用户
    User selectByUsername(String username);
}
