package com.baiyu.wxpublic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * class Name:
 *
 * @auther baiyu
 * @date 2020/7/23
 * @Email baixixi187@163.com
 */
public class JsonUtils {
    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(obj);
    }
}
