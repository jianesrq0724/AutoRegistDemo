package com.carl.mvpdemo.module.home.bean;

/**
 * @author Carl
 * version 1.0
 * @since 2018/7/5
 */
public class CaptchaBean {

    /**
     * Result : 答题结果
     * Id : 题目Id(报错使用)
     */

    private String Result;
    private String Id;

    public String getResult() {
        return Result;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }
}
