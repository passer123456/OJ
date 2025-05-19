package com.onlinejudge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.onlinejudge.entity.Comment;
import com.onlinejudge.entity.CommentLike;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 插入评论
    int insert(Comment comment);
    
    // 根据ID查询评论
    Comment selectById(Long id);
    
    // 查询问题的顶级评论
    List<Comment> selectTopLevelByProblemId(Long problemId);
    
    // 查询子评论
    List<Comment> selectRepliesByParentId(Long parentId);
    
    // 逻辑删除评论
    int deleteById(@Param("id") Long id);
    
    // 更新评论内容
    int updateContentByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId, @Param("content") String content);

    // 根据问题ID和用户ID查询评论
    List<Comment> selectByCondition(@Param("problemId") Long problemId, 
                              @Param("userId") Long userId);

    // 更新评论
    int updateById(Comment comment);

    // 新增点赞相关方法
    int insertLike(CommentLike commentLike);
    int deleteLike(@Param("commentId") Long commentId, @Param("userId") Long userId);
    int countLikes(Long commentId);
    List<Long> selectLikedUserIds(Long commentId);
    boolean existsLike(@Param("commentId") Long commentId, @Param("userId") Long userId);
}
