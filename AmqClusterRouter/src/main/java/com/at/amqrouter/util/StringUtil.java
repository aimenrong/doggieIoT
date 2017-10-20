package com.at.amqrouter.util;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
public class StringUtil {

    public static String extractServiceId(String str) {
        if (str != null && str.contains("://")) {
            return str.split("://")[1];
        }
        return null;
    }
}
