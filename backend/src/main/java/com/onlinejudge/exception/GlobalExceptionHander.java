package com.onlinejudge.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onlinejudge.utils.Result;

@ControllerAdvice("com.onlinejudge.controller")
public class GlobalExceptionHander {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHander.class);


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(Exception e) {
        // 记录异常日志
        log.error("服务器内部错误", e);

        // 处理异常并返回统一的错误响应
        return Result.error("服务器内部错误");
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result handleCustomException(CustomException e) {
        // 记录异常日志
        log.error("自定义异常", e);

        // 处理异常并返回统一的错误响应
        return Result.error(e.getCode(),e.getMessage());
    }
}
