package com.onlinejudge.utils;

import lombok.Data;

@Data
public class LoginInfo {
    private Integer userId; // 用户唯一标识ID
    private String username; // 用户名
    private String role; // 用户角色
    private String avatar; // 头像URL
    private String token; // JWT令牌
}
