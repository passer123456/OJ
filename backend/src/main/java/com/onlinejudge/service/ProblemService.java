package com.onlinejudge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.Problem;
import com.onlinejudge.mapper.ProblemMapper;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    public List<Problem> selectAll() {
        return problemMapper.selectAll();
    }

    public PageInfo<Problem> selectPage(String keyword,String difficulty,String tag,Integer page, Integer size) {
        PageHelper.startPage(page, size);
        
        List<Problem> list = problemMapper.selectProblemList(keyword, difficulty, tag);
        
        return PageInfo.of(list);
    }

    public void add(Problem problem) {
        problemMapper.insert(problem);
    }

    public void delete(int id) {
        problemMapper.delete(id);
    }

    public Problem selectById(int id) {
        return problemMapper.selectById(id);
    }
}
