package com.onlinejudge.entity;

import lombok.Data;

@Data
public class ProblemQueryDTO {
    private String keyword;     // 搜索关键词
    private String difficulty;  // 难度筛选
    private String tag;         // 标签筛选
    private Integer page;       // 当前页码
    private Integer size;       // 每页大小
}