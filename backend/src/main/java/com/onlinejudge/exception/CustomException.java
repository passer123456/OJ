package com.onlinejudge.exception;

public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private Integer code = 400; // 默认错误码

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
    
}
