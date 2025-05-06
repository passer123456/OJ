package com.onlinejudge.entity;

import java.util.List;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Comment {
    private Long id;       // 评论ID
    private String content;       // 评论内容
    private Long userId;          // 评论作者ID
    private String userName;      // 评论作者姓名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;      // 创建时间
    private Integer isDelete;     // 是否删除（0：未删除；1：已删除）

    private int blogId;      // 博客ID
    private Long parentId;    // 父评论ID（被回复的评论）
    private Long rootParentId;      // 根评论ID（最顶级的评论）

    private List<Comment> child;    // 本评论下的子评论
}
