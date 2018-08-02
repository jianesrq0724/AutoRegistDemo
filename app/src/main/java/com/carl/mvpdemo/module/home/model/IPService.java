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
public interface IPService {
    @GET("ic.asp")
    Flowable<ResponseBody> getIP(@QueryMap TreeMap<String, String> map);

}
