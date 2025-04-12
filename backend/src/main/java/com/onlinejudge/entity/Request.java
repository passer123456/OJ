package com.onlinejudge.entity;

import lombok.Data;

@Data
public class Request {
    private int id;
    private String code;
    private int userId;
    private String testCode;
    private String userCode;

    private String mergeCode(String userCode, String testCode) {
        int pos = userCode.lastIndexOf("}");
        if (pos == -1) {
            return null;
        }
        return userCode.substring(0, pos) + testCode + "}";
    }

    public String generateMergedCode() {
        return mergeCode(this.userCode, this.testCode);
    }
}
