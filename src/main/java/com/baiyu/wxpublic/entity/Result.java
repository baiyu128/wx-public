package com.baiyu.wxpublic.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * class Name:
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */
@Data
@NoArgsConstructor
public class Result {

    @ApiModelProperty(value = "是否成功",example = "true")
    private boolean success;//是否成功
    @ApiModelProperty(value = "返回码", example = "200")
    private Integer code;// 返回码
    @ApiModelProperty(value = "返回信息", example = "请求成功")
    private String message;//返回信息
    @ApiModelProperty(value = "返回数据")
    private Object data;// 返回数据

    public Result(ResultCode code) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
    }

    public Result(ResultCode code, Object data) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
        this.data = data;
    }

    public Result(Integer code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public static Result SUCCESS() {
        return new Result(ResultCode.SUCCESS);
    }
    public static Result SUCCESS(String message) {
        return new Result(200,message,true);
    }
    public static Result FAIL() {
        return new Result(ResultCode.FAIL);
    }
    //失败，自定义错误信息
    public static Result FAIL(String message) {
        return new Result(417,message,false);
    }

    public static Result SuccessData(Object data) {
        return new Result(ResultCode.SUCCESS,data);
    }
}
