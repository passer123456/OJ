package com.onlinejudge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.onlinejudge.entity.Comment;
import com.onlinejudge.entity.CommentLike;
import com.onlinejudge.mapper.CommentMapper;


@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    
    // 添加评论
    @Transactional
    public Comment addComment(Comment comment) {
        commentMapper.insert(comment);
        return commentMapper.selectById(comment.getId());
    }
    
    // 获取问题下的评论树
    public List<Comment> getCommentTreeByProblemId(Long problemId) {
        // 获取顶级评论
        List<Comment> topLevelComments = commentMapper.selectTopLevelByProblemId(problemId);
        
        // 为每个顶级评论获取子评论
        return topLevelComments.stream()
                .peek(comment -> {
                    List<Comment> replies = commentMapper.selectRepliesByParentId(comment.getId());
                    comment.setReplies(replies);
                })
                .collect(Collectors.toList());
    }
    
    // 删除评论
    @Transactional
    public boolean deleteComment(Long id) {
        return commentMapper.deleteById(id) > 0;
    }
    
    // 更新评论
    @Transactional
    public boolean updateComment(Long id, Long userId, String content) {
        return commentMapper.updateContentByIdAndUserId(id, userId, content) > 0;
    }
    

    @Transactional
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        if (commentMapper.existsLike(commentId, userId)) {
            return false;
        }
        
        // 插入点赞记录
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        commentMapper.insertLike(like);
        
        // 更新评论点赞数
        int likeCount = commentMapper.countLikes(commentId);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setLikeCount(likeCount);
        commentMapper.updateById(comment);
        
        return true;
    }


    @Transactional
    public boolean unlikeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        if (!commentMapper.existsLike(commentId, userId)) {
            return false;
        }
        
        // 删除点赞记录
        commentMapper.deleteLike(commentId, userId);
        
        // 更新评论点赞数
        int likeCount = commentMapper.countLikes(commentId);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setLikeCount(likeCount);
        commentMapper.updateById(comment);
        
        return true;
    }


    public int getLikeCount(Long commentId) {
        return commentMapper.countLikes(commentId);
    }


    public List<Long> getLikedUserIds(Long commentId) {
        return commentMapper.selectLikedUserIds(commentId);
    }


    public boolean hasLiked(Long commentId, Long userId) {
        return commentMapper.existsLike(commentId, userId);
    }

}
