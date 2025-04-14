package com.onlinejudge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") 
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("token") // 允许前端获取token
                .allowCredentials(true) // 允许携带 Cookie
                .maxAge(3600);
    }
}