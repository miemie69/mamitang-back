package com.mamitang;

import cn.jpush.api.JPushClient;

/**
 * masterSecret, appKey, 3
 * Created by lyy on 11/4/15.
 */
public class JPushUtils {
    private static String masterSecret = "masterSecret";
    private static String appKey = "appKey";
    private static JPushClient client = new JPushClient(masterSecret,appKey);
    public static JPushClient getClient(){
        return client;
    }
}
