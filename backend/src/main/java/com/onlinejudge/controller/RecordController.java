package com.onlinejudge.controller;

import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.Record;
import com.onlinejudge.entity.Request;
import com.onlinejudge.entity.Answer;
import com.onlinejudge.service.RecordService;
import com.onlinejudge.utils.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/record")
public class RecordController {
    @Autowired
    RecordService recordService;

    @GetMapping("/selectPage")
    public Result selectPage(
        @RequestParam(required = false) Integer userId,
        @RequestParam(required = false) Integer problemId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    ){
        PageInfo<Record> pageInfo = recordService.selectPage(userId,problemId,pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/select")
    public Result select(
        @RequestParam(required = false) Integer userId,
        @RequestParam(required = false) Integer problemId
    ) {
        List<Record> records = recordService.selectPage(userId, problemId);
        return Result.success(records);
    }

    @GetMapping("/test")
    public Result test(@RequestBody Request request) {
        try {
            Answer answer = recordService.test(request);
            return Result.success(answer);
        } catch (IOException | InterruptedException e) {
            return Result.error("Error occurred: " + e.getMessage());
        }
    }
    
    @PostMapping("/submit")
    public Result add(@RequestBody Request request) throws IOException, InterruptedException {
        Answer answer = recordService.add(request);
        return Result.success(answer);
    }

    @DeleteMapping("/delete/{id}")
	public Result delete(@PathVariable int id)
	{
		recordService.delete(id);
		return Result.success("删除成功");
	}
    
    @GetMapping("/{id}")
    public Result getProblemById(@PathVariable int id) {
        Record record = recordService.selectById(id);
    return Result.success(record);
    }

    @GetMapping("/getStatus")
    public Result getStatus(
        @RequestParam(required = false) Integer userId
    ) {
        List<Map<String,Object>> statusList = recordService.getLatestRecord(userId);
        return Result.success(statusList);
    }
}
