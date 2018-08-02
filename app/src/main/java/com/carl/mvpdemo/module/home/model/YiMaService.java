package com.carl.mvpdemo.module.home.model;

import java.util.TreeMap;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public interface YiMaService {
    @GET("UserInterface.aspx")
    Flowable<ResponseBody> login(@QueryMap TreeMap<String, String> map);

    @GET("UserInterface.aspx")
    Flowable<ResponseBody> getSms(@QueryMap TreeMap<String, String> map);

    @GET("UserInterface.aspx")
    Flowable<ResponseBody> getmobile(@QueryMap TreeMap<String, String> map);

    @GET("UserInterface.aspx")
    Flowable<ResponseBody> addIgnore(@QueryMap TreeMap<String, String> map);
}
