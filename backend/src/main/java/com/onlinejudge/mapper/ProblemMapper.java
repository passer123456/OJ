package com.onlinejudge.mapper;

import com.onlinejudge.entity.Problem;


import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProblemMapper 
{
    List<Problem> selectAll();

    // 分页查询问题列表
    List<Problem> selectProblemList(
        @Param("keyword") String keyword,
        @Param("difficulty") String difficulty,
        @Param("tag") String tag);
    
    // 插入问题
    void insert(Problem problem);

    // 删除问题
    void delete(int id);

    // 根据ID查询问题
    Problem selectById(int id);
}