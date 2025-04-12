package com.onlinejudge;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.pagehelper.PageInterceptor;

@SpringBootApplication
@MapperScan("com.onlinejudge.mapper")
public class OJSystem
{
	@Bean
	public PageInterceptor pageInterceptor() {
    	return new PageInterceptor();
	}
	
	public static void main(String[] args)
	{
		SpringApplication.run(OJSystem.class, args);
	}

}
