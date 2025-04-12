package com.onlinejudge.entity;

import lombok.Data;

@Data
public class Problem {
    private int problemid; // 题目唯一标识ID
    private String title; // 题目标题
    private String description; // 题目描述
    private String templateCode; // 模板代码
    private String testCode; // 测试代码
    private String sampleInput; // 样例输入
    private String sampleOutput; // 样例输出
    private String difficulty; // 题目难度
    private String tags; // 题目标签(逗号分隔)
    private int submitCount; // 提交次数
    private int acceptCount; // 通过次数
}

