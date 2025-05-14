package com.onlinejudge.entity;

import lombok.Data;

@Data
public class CommentLike {
    private Long id;
    private Long commentId;
    private Long userId;
}
