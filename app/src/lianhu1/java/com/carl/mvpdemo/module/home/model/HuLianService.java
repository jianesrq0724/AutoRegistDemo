package com.carl.mvpdemo.module.home.model;

import com.carl.mvpdemo.module.home.bean.RegisterBean;
import com.carl.mvpdemo.module.home.bean.SmsBean;
import com.carl.mvpdemo.pub.network.bean.ResBase;

import java.util.TreeMap;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public interface HuLianService {
    @Multipart
    @POST("sms.php")
    Flowable<ResBase> reqSms(@PartMap TreeMap<String, RequestBody> map);

    @Multipart
    @POST("h_register.php")
    Flowable<ResBase> register(@PartMap TreeMap<String, RequestBody> map);
}
