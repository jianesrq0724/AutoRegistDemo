package com.carl.mvpdemo.module.home.bean;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public class SmsBean {

    /**
     * code : 200
     * msg : 短信验证码发送成功
     */

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
