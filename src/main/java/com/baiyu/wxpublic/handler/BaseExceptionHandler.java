package com.baiyu.wxpublic.handler;

import com.baiyu.wxpublic.entity.Result;
import com.baiyu.wxpublic.entity.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 自定义的公共异常处理器
 * 1.声明异常处理器
 * 2.对异常统一处理
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {


    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public Result error(IOException e){
        log.error(e.getMessage(), e.getCause());
        return new Result(ResultCode.SERVER_ERROR);
    }
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result error(NullPointerException e){
        log.error(e.getMessage(), e.getCause());
        return new Result(ResultCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        log.error(e.getMessage(), e.getCause());
        return new Result(ResultCode.SERVER_ERROR);
    }
}
