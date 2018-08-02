package com.carl.mvpdemo.module.home.model;

import com.carl.mvpdemo.module.home.ConstantData;
import com.carl.mvpdemo.pub.network.HttpClient;
import com.carl.mvpdemo.pub.network.HttpParam;
import com.carl.mvpdemo.pub.network.bean.ResBase;

import java.util.TreeMap;

import io.reactivex.Flowable;
import okhttp3.RequestBody;

public class HuLianDataCenter {
    private static HuLianService mService;
    private String baseUrl = "https://www.chainhoo.com/interface/";


    private static final HuLianDataCenter ourInstance = new HuLianDataCenter();

    public static HuLianDataCenter getInstance() {
        return ourInstance;
    }

    private HuLianDataCenter() {
        mService = HttpClient.getInstance().createService(HuLianService.class, baseUrl);
    }

    public Flowable<ResBase> register(String phone, String code) {
        TreeMap<String, RequestBody> treeMap = new TreeMap<>();
        treeMap.put("type", HttpParam.getRequestBody("bind"));
        treeMap.put("phone", HttpParam.getRequestBody(phone));
        treeMap.put("code", HttpParam.getRequestBody(code));
        treeMap.put("uid", HttpParam.getRequestBody(ConstantData.INVITATION_CODE));
        return mService.register(treeMap);
    }

    public Flowable<ResBase> reqSms(String phone) {
        TreeMap<String, RequestBody> treeMap = new TreeMap<>();
        treeMap.put("phone", HttpParam.getRequestBody(phone));
        return mService.reqSms(treeMap);
    }

}
