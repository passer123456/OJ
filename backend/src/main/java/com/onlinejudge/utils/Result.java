package com.onlinejudge.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result
{
	//统一后端发送数据的格式
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(Object data)
    {
        return new Result(200, "success", data);
    }

    public static Result error(String msg)
    {
        return new Result(500, msg, null);
    }

    public static Result error(Integer code,String msg)
    {
        return new Result(code, msg, null);
    }
}
