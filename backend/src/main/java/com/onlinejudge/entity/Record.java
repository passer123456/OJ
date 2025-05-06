package com.onlinejudge.entity;

import lombok.Data;

// 记录
@Data
public class Record {
    private int submitId; // 提交记录的唯一标识符
    private int problemId; // 关联的题目ID
    private int userId; // 提交者的用户ID
    private String code; // 提交的代码
    private String language; // 编程语言
    private int score=0;
    private  String memory;
    private  String time;
    private String submitTime;
    private String result; // 提交结果
}
