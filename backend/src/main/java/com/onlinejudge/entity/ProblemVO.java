package com.onlinejudge.entity;

import java.util.List;

import lombok.Data;

@Data
public class ProblemVO {
    private Integer id;         // 题号
    private String title;       // 标题
    private String difficulty;  // 难度
    private List<String> tags;  // 标签列表
    private String acRate;     // 通过率
    private Integer submitCount; // 提交数
}
