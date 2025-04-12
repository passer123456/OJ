package com.onlinejudge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

// @Configuration
// public class CorsConfig implements WebMvcConfigurer {
//     @Override
//     public void addCorsMappings(@NonNull CorsRegistry registry) {
//         registry.addMapping("/**") // 允许所有路径
//             .allowedOrigins("http://localhost:5173") // 允许的前端地址
//             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
//             .allowedHeaders("*") // 允许所有请求头
//             .allowCredentials(true); // 允许携带 Cookie
//     }
// }


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许所有来源，生产环境应指定具体域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}