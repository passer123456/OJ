package com.onlinejudge.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinejudge.entity.Answer;
import com.onlinejudge.entity.Record;
import com.onlinejudge.entity.Request;
import com.onlinejudge.mapper.RecordMapper;
import com.onlinejudge.utils.Task;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecordService {

    @Autowired
    private RecordMapper recordMapper;

    public PageInfo<Record> selectPage(Integer userId,Integer problemId,Integer page, Integer size) {
        PageHelper.startPage(page, size);
        
        List<Record> list = recordMapper.selectPage(userId, problemId);
        
        return PageInfo.of(list);
    }

    public List<Record> selectPage(Integer userId,Integer problemId) {
        return recordMapper.selectPage(userId, problemId);
    }

    public Answer test(Request request) throws IOException, InterruptedException {
        request.generateMergedCode();
        Task task = new Task();
        Answer answer = task.compileAndRun(request);
        return answer;
    }

    public Answer add(Request request) throws IOException, InterruptedException {
        request.generateMergedCode();
        Task task = new Task();
        Answer answer = task.compileAndRun(request);
        Record record = new Record();
        record.setUserId(request.getUserId());
        record.setProblemId(request.getId());
        record.setCode(request.getUserCode());
        record.setLanguage(request.getLanguage());
        record.setMemory(answer.getMemory());
        record.setTime(answer.getRuntime());
        record.setScore(answer.getScore());
        record.setResult(answer.getScore() == 100 ? "solved" : "unsolved");
        record.setSubmitTime(LocalDateTime.now().toString());
        recordMapper.insert(record);
        return answer;
    }

    public void delete(int id) {
        recordMapper.delete(id);
    }

    public Record selectById(int id) {
        return recordMapper.selectById(id);
    }

    public List<Map<String, Object>> getLatestRecord(int userId) {
        List<Record> records = recordMapper.getLatestRecordsByUserId(userId);
        if (records.isEmpty()) {
            return null;
        }
        return records.stream()
            .map(record -> {
                Map<String, Object> map = new HashMap<>();
                map.put("problemId", record.getProblemId());
                map.put("status", record.getResult());
                return map;
            })
            .collect(Collectors.toList()); // 收集成 List
    }
}
