package com.baiyu.wxpublic.controller;

import com.baiyu.wxpublic.service.MessageHandleService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * class Name:
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */
@Api(tags = "微信API")
@RestController
@RequestMapping("/wx")
@Slf4j
@AllArgsConstructor
public class WxController {

    @Resource
    private MessageHandleService messageHandleService;

    @GetMapping(value = "/handler")
    public String echoStr(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        return messageHandleService.echoStr(signature, timestamp, nonce, echostr);
    }

    /**
     * 校验信息是否是从微信服务器发出，处理消息
     *
     */
    @PostMapping(value = "/handler")
    public String processPost(@RequestBody String requestBody,
                              @RequestParam("signature") String signature,
                              @RequestParam("timestamp") String timestamp,
                              @RequestParam("nonce") String nonce,
                              @RequestParam("openid") String openid,
                              @RequestParam(name = "encrypt_type", required = false) String encType,
                              @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        return messageHandleService.processMsg(openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
    }

}
