package com.carl.mvpdemo.pub.network;

import android.text.TextUtils;

import com.carl.mvpdemo.pub.utils.SPKeyUtils;
import com.carl.mvpdemo.pub.utils.SPUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/10
 */
public class CookieHttpClient {

    private static final CookieHttpClient INSTANCE = new CookieHttpClient();
    private Map<String, Object> mServiceMap = new HashMap<>();

    public static CookieHttpClient getInstance() {
//        return HttpClient.INSTANCE;
        // TODO: 2018/7/5 取消单例，user-anger和cookie
        return new CookieHttpClient();
    }

    private CookieHttpClient() {

    }


    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = "";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        T service = retrofit.create(serviceClass);
        mServiceMap.put(serviceClass.getName() + baseUrl, service);
        return retrofit.create(serviceClass);
//        }

    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .sslSocketFactory(SSLSocketClient.getSslFactory())
            .readTimeout(60000, TimeUnit.MILLISECONDS)
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .removeHeader("User-Agent")
                            .addHeader("User-Agent", getUserAgent())
                            .build();

                    Response originalResponse = chain.proceed(request);
                    if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                        List<String> cookieList = originalResponse.headers("Set-Cookie");
                        String[] cookieArray = cookieList.get(0).split(";");
                        SPUtils.getInstance().put(SPKeyUtils.COOKIE, cookieArray[0]);
                    }
                    return originalResponse;
                }
            })
            .build();

    private String getUserAgent() {
        String userAgent = "Mozilla/5.0 (Linux; Android 5.1.1; Coolpad 8675-F01 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044113 Mobile Safari/537.36 MicroMessenger/6.6.7.1321(0x26060736) NetType/WIFI Language/zh_CN";
        userAgent = userAgent.replace("8675", String.valueOf(new Random().nextInt(8000) + 1000));
        return userAgent;
    }

}
