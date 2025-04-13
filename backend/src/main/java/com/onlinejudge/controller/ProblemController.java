package com.onlinejudge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlinejudge.service.ProblemService;
import com.onlinejudge.utils.Result;
import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.Problem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/problem")
public class ProblemController {
    @Autowired
    ProblemService problemService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Problem> problems = problemService.selectAll();
        return Result.success(problems);
    }

    @GetMapping("/selectPage")
    public Result selectPage(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String difficulty,
        @RequestParam(required = false) String tag,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    ){
        PageInfo<Problem> pageInfo = problemService.selectPage(keyword, difficulty, tag, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Problem problem) {
        problemService.add(problem);
        return Result.success("添加成功");
    }

    @DeleteMapping("/delete/{id}")//以ID删除题目
	public Result delete(@PathVariable int id)
	{
		problemService.delete(id);
		return Result.success("删除成功");
	}
    
    @GetMapping("/{id}")
    public Result getProblemById(@PathVariable int id) {
        Problem problem = problemService.selectById(id);
    return Result.success(problem);
}
}
