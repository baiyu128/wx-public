package com.baiyu.wxpublic.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baiyu.wxpublic.config.WxMpProperties;
import com.baiyu.wxpublic.entity.Result;
import com.baiyu.wxpublic.entity.ResultCode;
import com.baiyu.wxpublic.service.WeiXinPushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.stereotype.Service;
import weixin.popular.api.MenuAPI;
import weixin.popular.api.ShorturlAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.menu.selfmenu.CurrentSelfmenuInfo;
import weixin.popular.bean.shorturl.Shorturl;
import weixin.popular.bean.token.Token;
import weixin.popular.bean.user.User;

/**
 * class Name:
 *
 * @auther baiyu
 * @date 2020/7/22
 * @Email baixixi187@163.com
 */
@Service
@Slf4j
@AllArgsConstructor
public class WeiXinPushServiceImpl implements WeiXinPushService {

    private final WxMpProperties properties;

    private static final String KEY_PRE="access_token_";
    private static final String TEST_URL = "https://www.baidu.com";

    /*
     * 入场推送公众号消息
     * */
    public void inPush(String parkingId, String openId, String aa, String bb,String cc) {
        //2,推送消息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)//要推送的用户openid
                .templateId("")//模版id
                .url("https://") //点击模版消息要访问的网址
                .build();
        //3,如果是正式版发送模版消息，这里需要配置你的信息
        templateMessage.addData(new WxMpTemplateData("first", "祝您使用愉快！", "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword1", aa, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword2", bb, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword3", cc, "#15187B"));
//        templateMessage.addData(new WxMpTemplateData("remark", "value2", "#BB09FD"));

        pushMsg(templateMessage);
    }

    /*
     * 出场推送公众号消息
     * */
    public void outPush(String no, String openId, String aa,String bb, String cc,String dd) {
        //2,推送消息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)//要推送的用户openid
                .templateId("")//模版id
                .url("https://")//点击模版消息要访问的网址 订单详情
                .build();
        //3,如果是正式版发送模版消息，这里需要配置你的信息
        String parkTime = "";
        templateMessage.addData(new WxMpTemplateData("first", "祝您平安!", "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword1", aa, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword2", bb, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword3", cc, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword4", dd, "#15187B"));
        templateMessage.addData(new WxMpTemplateData("keyword5", parkTime, "#15187B"));
//        templateMessage.addData(new WxMpTemplateData("remark", "value2", "#BB09FD"));

        pushMsg(templateMessage);
    }

    /**
     * 推送公众号消息
     * @param msg
     */
    public void pushMsg(WxMpTemplateMessage msg) {
        //1，配置
        WxMpDefaultConfigImpl wxStorage = new WxMpDefaultConfigImpl();
        wxStorage.setAppId(properties.getConfigs().get(0).getAppId());
        wxStorage.setSecret(properties.getConfigs().get(0).getSecret());
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxStorage);

        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(msg);
        } catch (Exception e) {
            log.error("推送微信公众号消息失败：{}",e.getMessage());
        }
    }

    /**
     * 获取微信access_token
     * @return
     */
    public String getAccessToken(){
        String key = KEY_PRE+properties.getConfigs().get(0).getAppId();
//        String access_token = stringRedisTemplate.opsForValue().get(key);
        String access_token = null;
        if (null == access_token) {
            access_token = requestToken(key);
        }
        // 返回之前验证该access_token是否已过期

        Shorturl shorturl = ShorturlAPI.shorturl(access_token, TEST_URL);
        if ("40001".equals(shorturl.getErrcode())){
            // access_token已过期，刷新缓存
            log.error("access_token已过期，重新请求中。。。。");
            access_token = requestToken(key);
        }
        return access_token;
    }

    private String requestToken(String key) {
        Token token = TokenAPI.token(properties.getConfigs().get(0).getAppId(), properties.getConfigs().get(0).getSecret());
        if (null != token.getErrcode() ){
            log.error("获取access_token失败：appId_{},secret_{},code_{},msg_{}",properties.getConfigs().get(0).getAppId(), properties.getConfigs().get(0).getSecret(),token.getErrcode(),token.getErrmsg());
            return "";
        }
        String  access_token = token.getAccess_token();
        return access_token;
    }

    /**
     * 获取公众号粉丝信息
     */
    public User getUserInfo(String access_token, String openid){
        User info = UserAPI.userInfo(access_token, openid);
        if (null == info) {
            log.error("获取公众号粉丝信息失败!");
            return new User();
        }
        if (null != info.getErrcode()){ // 可能accessToken过期，再试一次
            log.error("获取公众号粉丝信息失败：code_{},msg_{},accessToken_{}", info.getErrcode(),info.getErrmsg(),access_token);
            int i = 3;
            while (i>0){
                info = getUserInfo(access_token, openid);
                if (null == info.getErrcode()){
                    return info;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    log.error("sleep error:{}", e.getMessage());
                }
                i--;
            }
        }
        return info;
    }

    /**
     * 获取微信公众号二维码,永久二维码 id和str选填其中一个，另一个为null
     * @param codeType 二维码类型 "1": 临时二维码  "2": 永久二维码
     * @param sceneId 场景值ID
     * @param sceneStr 场景值字符串 1-64位  生成永久码的规则：例：5_出口2  5为车场在数据库的id值，即第5条数据，出口2为出口车道
     * @param fileName 图片名称
     */
    public Result getWXPublicQRCode(String codeType, Integer sceneId, String sceneStr, String fileName) {

//        String wxAccessToken = getAccessToken();
//        String  ticket = "";
//        QrcodeTicket temp = new QrcodeTicket();
//        if ("1".equals(codeType)) { // 临时二维码
//            int expire_seconds = 3600;
//            temp = QrcodeAPI.qrcodeCreateTemp(wxAccessToken, expire_seconds, Long.parseLong(sceneId.toString()));
//        } else if ("2".equals(codeType) && null !=sceneId) { // 永久二维码
//            temp = QrcodeAPI.qrcodeCreateFinal(wxAccessToken, sceneId);
//        } else if ("2".equals(codeType) && null != sceneStr){
//            temp = QrcodeAPI.qrcodeCreateFinal(wxAccessToken, sceneStr);
//        }
//        if (temp.getErrcode() != null){
//            log.error("获取二维码失败：{}_{}",temp.getErrcode(),temp.getErrmsg());
//            return new Result(ResultCode.FAIL,temp.getErrmsg());
//        }
//        ticket = temp.getTicket();
//        BufferedImage showqrcode = QrcodeAPI.showqrcode(ticket);
//        // WXConstants.QRCODE_SAVE_URL: 填写存放图片的路径
//        String dirName = "";
//        if ("1".equals(codeType)){
//            dirName = "tempQr/";
//        } else if ("2".equals(codeType)){
//            dirName = "FinalQr/";
//        }
//        String filePath = System.getProperty("user.dir")+sonPath+dirName;
//        log.debug("获取到的公众号二维码的文件路径:{}", filePath );
//        log.debug("二维码整个图片路径：{}:{}{}{}",host,port,sonPath+dirName,fileName);
//        String imgUrl = "http://"+host+":"+port+sonPath+dirName+fileName+".png";
//        //创建文件路径
//        File dest = new File(filePath + fileName+".png");
//        // 检测是否存在目录
//        if (!dest.getParentFile().exists()) {
//            //假如文件不存在即重新创建新的文件已防止异常发生
//            dest.getParentFile().mkdirs();
//        }
//        try {
//            ImageIO.write(showqrcode,"PNG", dest);
//        } catch (IOException e) {
//            log.error("二维码图片保存失败：" + e.getMessage());
//            return new Result(ResultCode.FAIL,"二维码图片保存失败：" + e.getMessage());
//        }
        return new Result(ResultCode.SUCCESS);

    }

    /**
     * 长链接转短链接
     * @param oldUrl
     * @return
     */
    public String long2short(String oldUrl){
        String accessToken = getAccessToken();
        Shorturl shorturl = ShorturlAPI.shorturl(accessToken, oldUrl);
        if (shorturl.getErrcode()!=null && !shorturl.getErrcode().equals("0")){
            log.error("短链接转换失败：code_{},msg_{}",shorturl.getErrcode(),shorturl.getErrmsg());
            return oldUrl;
        }
        return shorturl.getShort_url();
    }

    /**
     * 创建自定义菜单
     * @param menuJson
     * @return
     */
    public Result menuCreate(String menuJson) {
        String accessToken = getAccessToken();
        JSONObject jsonObject = JSONObject.parseObject(menuJson);
        menuJson = JSONObject.toJSONString(jsonObject);
        log.info("创建自定义菜单：{}", menuJson);
        BaseResult result = MenuAPI.menuCreate(accessToken, menuJson);
        if (null != result && "0".equals(result.getErrcode())){
            return Result.SUCCESS("创建自定义菜单成功!");
        } else {
            if (null == result) return Result.FAIL("创建自定义菜单为null");
            log.error("创建自定义菜单失败：{}_{}", result.getErrcode(),result.getErrmsg());
            return Result.FAIL("创建自定义菜单失败!");
        }
    }

    /**
     * 查询自定义菜单
     * @return
     */
    public Result menuInfo(){
        String accessToken = getAccessToken();
        CurrentSelfmenuInfo current_selfmenu_info = MenuAPI.get_current_selfmenu_info(accessToken);
        if (null == current_selfmenu_info) return Result.FAIL("查询失败");
        if (current_selfmenu_info.getIs_menu_open() == 0){
            return Result.FAIL("菜单尚未开启，请联系管理员！");
        } else {
            String  json = JSONObject.toJSON(current_selfmenu_info.getSelfmenu_info()).toString();
            return Result.SuccessData(json);
        }
    }

    /**
     * 删除自定义菜单
     * @return
     */
    public Result delMenu() {
        String accessToken = getAccessToken();
        BaseResult result = MenuAPI.menuDelete(accessToken);
        if (!result.getErrcode().equals("0")){
            return Result.FAIL("删除菜单失败！");
        }
        return Result.SUCCESS();
    }
}
