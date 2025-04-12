package com.onlinejudge.entity;

import lombok.Data;

@Data
public class User {
    private Integer userId; // 用户唯一标识ID
    private String username; // 用户名
    private String password; // 密码(加密存储)
    private String role; // 用户角色
    private String avatar; // 头像URL
    private String bio; // 个人简介
}