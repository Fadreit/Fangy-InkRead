package com.fadreit.inkreadboot.Config;

import cn.dev33.satoken.exception.NotLoginException;
import com.fadreit.inkreadboot.Common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice // 全局异常处理
public class ExceptionHandle {
    /**
     * 处理未登录异常
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(NotLoginException.class)
    public Result handleNotLoginException(NotLoginException e) {
        log.warn("未登录访问：{}", e.getMessage());
        return Result.error(401, "未登录或登录已过期");
    }

    /**
     * 处理其他异常
     * @param e 异常
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "服务器内部错误");
    }

}
