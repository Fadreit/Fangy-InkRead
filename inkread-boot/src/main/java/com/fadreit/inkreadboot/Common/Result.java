package com.fadreit.inkreadboot.Common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;//状态码
    private String message;//返回信息
    private Object data;//返回数据

    public static Result success(){
        return new Result(200,"操作成功",null);
    }
    public static Result success(Object data){
        return new Result(200,"操作成功",data);
    }
    public static Result error(){
        return new Result(500,"操作失败",null);
    }
    public static Result error(Integer code,String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

/*
 * 统一返回结果状态码
code    含义	       常见场景
200	    成功	       所有正常返回
400	    请求参数错误	   校验失败、业务规则不满足
401	    未登录	       Token缺失、过期或无效
403	    无权限	       角色不足、访问他人资源
404     资源不存在	   ID对应的记录未找到
500	    服务器内部错误  系统未知异常
 */
