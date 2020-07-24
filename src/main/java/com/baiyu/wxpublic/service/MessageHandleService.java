package com.baiyu.wxpublic.service;

import weixin.popular.bean.message.EventMessage;

import javax.servlet.http.HttpServletRequest;

/**
 * class Name:
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */

public interface MessageHandleService {

//    String handleMessage(EventMessage eventMessage);

    String echoStr(String signature, String timestamp, String nonce, String echostr);

    String processMsg(String openid, String signature, String encType, String msgSignature, String timestamp, String nonce, String requestBody);
}
