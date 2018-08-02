package com.carl.mvpdemo.module.home.model;

import com.carl.mvpdemo.module.home.ConstantData;
import com.carl.mvpdemo.pub.network.HttpClient;

import java.util.TreeMap;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;

import static com.carl.mvpdemo.pub.constant.YiMaConstant.YIMA_TOKEN;

public class YiMaDataCenter {
    private static final YiMaDataCenter ourInstance = new YiMaDataCenter();
    private static YiMaService service;
    private String baseUrl = "http://api.fxhyd.cn/interface/";

    public static YiMaDataCenter getInstance() {
        return ourInstance;
    }

    private YiMaDataCenter() {
        service = HttpClient.getInstance().createService(YiMaService.class, baseUrl);
    }

    public Flowable<ResponseBody> login(String username, String password) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("action", "login");
        treeMap.put("username", username);
        treeMap.put("password", password);
        return service.login(treeMap);
    }

    public Flowable<ResponseBody> getSms(String mobile) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("action", "getsms");
        treeMap.put("token", YIMA_TOKEN);
        treeMap.put("itemid", ConstantData.ITEM_ID);
        treeMap.put("mobile", mobile);
        return service.getSms(treeMap);
    }


    public Flowable<ResponseBody> getmobile() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("action", "getmobile");
        treeMap.put("token", YIMA_TOKEN);
        treeMap.put("province", "440000");
        treeMap.put("itemid", ConstantData.ITEM_ID);
        return service.getmobile(treeMap);
    }


    public Flowable<ResponseBody> addIgnore(String mobile) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("action", "addIgnore");
        treeMap.put("token", YIMA_TOKEN);
        treeMap.put("itemid", ConstantData.ITEM_ID);
        treeMap.put("mobile", mobile);
        return service.addIgnore(treeMap);
    }


}
