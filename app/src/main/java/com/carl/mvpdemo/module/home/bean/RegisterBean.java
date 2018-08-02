package com.carl.mvpdemo.module.home.bean;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public class RegisterBean {

    /**
     * code : 401
     * msg : 验证码过期
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
