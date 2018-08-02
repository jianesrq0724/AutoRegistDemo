package com.carl.mvpdemo.pub.network;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public class HttpParam {
    /**
     * 将String转为RequestBody
     *
     * @param value
     * @return
     */
    public static RequestBody getRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
}
