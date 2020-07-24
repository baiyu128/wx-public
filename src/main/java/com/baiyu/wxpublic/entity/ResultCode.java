package com.baiyu.wxpublic.entity;

/**
 * class Name:
 * 公共的返回码
 * 返回码code：
 * 成功：0
 * 失败：10001
 * 未登录：10002
 * 未授权：10003
 * 抛出异常：9999
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */
public enum ResultCode {

    SUCCESS(true, 0, "操作成功！"),
    FAIL(false, 9999, "操作失败"),
    SERVER_ERROR(false, 5000, "抱歉，系统繁忙，请稍后重试！"),
    ;

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
