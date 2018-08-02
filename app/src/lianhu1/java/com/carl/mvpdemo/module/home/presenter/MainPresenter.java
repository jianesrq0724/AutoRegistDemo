package com.carl.mvpdemo.module.home.presenter;

import com.carl.mvpdemo.module.home.model.HuLianDataCenter;
import com.carl.mvpdemo.pub.network.bean.ResBase;

import java.util.Random;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;

/**
 * @author Carl
 * @version 1.0
 * @since 2018/5/10
 */
public class MainPresenter extends MainBasePresenter {

    @Override
    public int getRegistInterval() {
        return new Random().nextInt(3 * 60) + 3 * 60;
    }

    @Override
    public Flowable<ResBase> reqSmsFlowable(String phone, String piccode) {
        return HuLianDataCenter.getInstance().reqSms(phone);
    }

    @Override
    public Flowable<ResBase> registerFlowable(String phone, String code, String piccode) {
        return HuLianDataCenter.getInstance().register(phone, code);
    }

    @Override
    public Flowable<ResponseBody> getCaptchaFlowable() {
        return null;
    }
}
