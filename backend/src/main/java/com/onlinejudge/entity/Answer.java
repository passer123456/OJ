package com.onlinejudge.entity;

import lombok.Data;

// 编译运行的结果
@Data
public class Answer {
    private int errno;// 错误码，0 表示编译运行都成功. 1 表示编译出错. 2 表示运行抛异常
    private String compile_error; // 出错原因
    private String stdout; // 程序的标准输出
    private String stderr; // 程序的标准错误
    private int score=0;
    private  String memory;
    private  String runtime;
}
