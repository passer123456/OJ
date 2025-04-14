package com.onlinejudge.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.onlinejudge.entity.User;
import com.onlinejudge.exception.CustomException;
import com.onlinejudge.service.UserService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TokenUtil {
    @Resource
    private UserService userService;

    static UserService userServiceStatic;

    @PostConstruct
    public void init() {
        userServiceStatic = userService;
    }

    private static final String SECRET_KEY = "your-strong-secret-key-123!@#";

    public static String createToken(String data) {
        return JWT.create()
                .withSubject(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1))  // 设置token有效期为1天
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public static User getCurrentUser(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if(StrUtil.isEmpty(token)){
            token = request.getParameter("token");
        }
        // 验证token
        if(StrUtil.isBlank(token)){
            throw new CustomException("无权限访问",401);
        }
        // 解析token
        User user = null;
        String userId = JWT.decode(token).getSubject().split("-")[0];
        user = userServiceStatic.selectById(Integer.parseInt(userId));
        if (user == null) {
            throw new CustomException("用户不存在",401);
        }else{return user;}
              
    }
}
