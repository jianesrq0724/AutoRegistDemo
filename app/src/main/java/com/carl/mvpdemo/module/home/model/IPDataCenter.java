package com.carl.mvpdemo.module.home.model;

import com.carl.mvpdemo.pub.network.HttpClient;

import java.util.TreeMap;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;

public class IPDataCenter {
    private static final IPDataCenter ourInstance = new IPDataCenter();
    private static IPService service;
    private String baseUrl = "http://2018.ip138.com/";

    public static IPDataCenter getInstance() {
        return ourInstance;
    }

    private IPDataCenter() {
        service = HttpClient.getInstance().createService(IPService.class, baseUrl);
    }


    public Flowable<ResponseBody> getIP() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        return service.getIP(treeMap);
    }


}
