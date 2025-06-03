package com.onlinejudge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${cors.allowed-origins}")  // 从配置文件读取
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins) // 允许的生产环境的前端地址
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("token") // 允许前端获取token
                .allowCredentials(true) // 允许携带 Cookie
                .maxAge(3600);
    }
}