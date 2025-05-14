package com.onlinejudge.entity;

import java.util.List;
import java.util.Date;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long problemId;
    private Long userId;
    private Long parentId;
    private String content;
    private Date createTime;
    private Date updateTime;
    private Integer status;
    private Integer likeCount;
    private List<Comment> replies;    // 本评论下的子评论
}
