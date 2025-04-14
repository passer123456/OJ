package com.onlinejudge.utils;

import com.onlinejudge.service.UserService;
import com.onlinejudge.entity.User;
import com.onlinejudge.exception.CustomException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import cn.hutool.core.util.StrUtil;


@Component
public class JWTInterceptor  implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }

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
        System.out.println("token: "+token);
        try {
            String userId = JWT.decode(token).getSubject().split("-")[0];
            user = userService.selectById(Integer.parseInt(userId));         
        } catch (Exception e) {
            throw new CustomException("token无效1",401);
        }
        if (user == null) {
            throw new CustomException("用户不存在",401);
        }
        try{
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("your-strong-secret-key-123!@#")).build();
            verifier.verify(token);
        } catch (Exception e) {
            throw new CustomException("token无效2",401);
        }
        return true;
    }
}
