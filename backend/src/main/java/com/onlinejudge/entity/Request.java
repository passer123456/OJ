package com.onlinejudge.entity;

import lombok.Data;

@Data
public class Request {
    private int id;
    private int userId;     
    private String code;
    private String language;
    private String testCode;
    private String userCode;

    private String mergeCode(String userCode, String testCode) {
        int pos = userCode.lastIndexOf("}");
        if (pos == -1) {
            return null;
        }
        return userCode.substring(0, pos) + testCode + "}";
    }

    public void generateMergedCode() {
        this.code=mergeCode(this.userCode, this.testCode);
    }
}
