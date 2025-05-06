package com.onlinejudge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.onlinejudge.entity.Record;

@Mapper
public interface RecordMapper {
    // 分页查询问题列表
    List<Record> selectPage(
        @Param("userId") Integer userId,
        @Param("problemId") Integer problemId
    );
    
    // 插入
    void insert(Record record);

    // 删除
    void delete(int id);

    // 根据ID查询
    Record selectById(int id);
}
