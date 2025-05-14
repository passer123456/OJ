package com.onlinejudge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.onlinejudge.entity.Comment;
import com.onlinejudge.service.CommentService;
import com.onlinejudge.utils.Result;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    
    // 发表评论
    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        Comment newComment=commentService.addComment(comment);
        return Result.success(newComment);
    }
    
    // 获取问题评论
    @GetMapping("/problem/{problemId}")
    public Result getCommentsByProblemId(@PathVariable Long problemId) {
        List<Comment> comments = commentService.getCommentTreeByProblemId(problemId);
        return Result.success(comments);
    }
    
    // 删除评论
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        boolean deleted = commentService.deleteComment(id, userId);
        if (!deleted) {
            return Result.error("Failed to delete comment or comment not found.");
        }
        return Result.success("Comment deleted successfully.");
    }
    
    // 更新评论
    @PutMapping("/{id}")
    public Result updateComment(@PathVariable Long id, 
                                @RequestParam Long userId,
                                @RequestBody String content) {
        boolean updated = commentService.updateComment(id, userId, content);
        if (!updated) {
            return Result.error("Failed to update comment or comment not found.");
        }
        return Result.success("Comment updated successfully.");
    }
    
    @PostMapping("/{commentId}/like")
    public boolean likeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        return commentService.likeComment(commentId, userId);
    }

    @DeleteMapping("/{commentId}/unlike")
    public boolean unlikeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        return commentService.unlikeComment(commentId, userId);
    }

    @GetMapping("/{commentId}/isliked")
    public Result hasLiked(@PathVariable Long commentId, @RequestParam Long userId) {
        boolean liked = commentService.hasLiked(commentId, userId);
        return Result.success(liked);
    }

}