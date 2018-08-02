package com.carl.mvpdemo.module.home.presenter;

import android.os.SystemClock;

import com.carl.mvpdemo.BaseApplication;
import com.carl.mvpdemo.BuildConfig;
import com.carl.mvpdemo.R;
import com.carl.mvpdemo.module.home.interfaces.MainI;
import com.carl.mvpdemo.module.home.model.YiMaDataCenter;
import com.carl.mvpdemo.pub.base.BasePresenter;
import com.carl.mvpdemo.pub.base.BaseSubscriber;
import com.carl.mvpdemo.pub.constant.GlobalConstant;
import com.carl.mvpdemo.pub.network.bean.ResBase;
import com.carl.mvpdemo.pub.utils.ClipboardUtils;
import com.carl.mvpdemo.pub.utils.LogUtils;
import com.carl.mvpdemo.pub.utils.RxUtils;
import com.carl.mvpdemo.pub.utils.SPKeyUtils;
import com.carl.mvpdemo.pub.utils.SPUtils;
import com.carl.mvpdemo.pub.utils.TimeUtils;
import com.carl.mvpdemo.pub.utils.ToastUtils;
import com.carl.mvpdemo.pub.webview.WebViewActivity;

import org.reactivestreams.Publisher;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * @author Carl
 * @version 1.0
 * @since 2018/5/10
 */
public abstract class MainBasePresenter extends BasePresenter<MainI> {

    public static String mInvelTag = "";


    /**
     * 随机生成时间间隔，单位S
     */
    public int getRegistInterval() {
        return new Random().nextInt(66 * 60);
    }

    public void reqSms(final String phone) {
        reqSms(phone, "");
    }


    int reqSmsFial = 0;

    public void reqSms(final String phone, final String piccode) {

        BaseSubscriber subscriber = new BaseSubscriber<ResBase>() {
            @Override
            protected void onUserSuccess(ResBase resBase) {
                mInvelTag = resBase.tag;
                mViewRef.get().changeHuLianState(phone + "请求验证码成功");
                getSms(15, phone, piccode);
            }

            @Override
            protected void onUserFail(ResBase resBase) {
                super.onUserFail(resBase);
                mViewRef.get().changeHuLianState(phone + "请求验证码失败");
                reqSmsFial++;
                if (reqSmsFial >= 6) {
                    reqSmsFial = 0;
                    getmobile(60 * 60);
                } else {
                    getmobile(6);
                }
            }
        };

        Disposable subscribe = reqSmsFlowable(phone, piccode)
                .compose(RxUtils.<ResBase>getScheduler(true, getView()))
                .retryWhen(new RetryWhenFunction())
                .subscribeWith(subscriber);

        mRxManage.add(subscribe);
    }

    public abstract Flowable<ResBase> reqSmsFlowable(String phone, String piccode);

    public void regist(String phone) {
        regist(phone, "");
    }

    public void regist(String phone, String code) {
        regist(phone, code, "");
    }

    public void regist(String phone, String code, String piccode) {
        regist(0, phone, code, piccode);
    }

    int registFailtCount;

    public void regist(int time, final String phone, final String code, final String piccode) {
        Flowable<Object> lFlowable = Flowable.timer(time, TimeUnit.SECONDS).flatMap(new Function<Long, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Long aLong) throws Exception {
                return registerFlowable(phone, code, piccode)
                        .compose(RxUtils.<ResBase>getScheduler(true, getView()))
                        .retryWhen(new RetryWhenFunction());
            }
        });

        final BaseSubscriber subscriber = new BaseSubscriber<ResBase>() {
            @Override
            protected void onUserSuccess(final ResBase resBase) {
                String toWeb = mContext.getString(R.string.register_to_web);
                if (toWeb.equals("1")) {
                    String cookie = SPUtils.getInstance().getString(SPKeyUtils.COOKIE);
                    registerToWeb(cookie);
                }

                int successCount = SPUtils.getInstance().getInt(SPKeyUtils.REGIST_SUCCESS);
                successCount++;
                SPUtils.getInstance().put(SPKeyUtils.REGIST_SUCCESS, successCount);
                mViewRef.get().refreshCount();
                setRegistCountDownTime(resBase, getRegistInterval());
            }

            @Override
            protected void onUserFail(ResBase resBase) {
                super.onUserFail(resBase);
                int failSuccess = SPUtils.getInstance().getInt(SPKeyUtils.REGIST_FAIL);
                failSuccess++;
                SPUtils.getInstance().put(SPKeyUtils.REGIST_FAIL, failSuccess);
                mViewRef.get().changeHuLianState("registFail" + resBase.msg);
                mViewRef.get().refreshCount();

                registFailtCount++;
                //失败超过3次休息1分钟重新注册
                if (registFailtCount >= 3) {
                    registFailtCount = 0;
                    getmobile(60);
                } else {
                    getmobile(6);
                }
            }

            @Override
            protected void onUserError(Throwable t) {
                super.onUserError(t);
                //解析错误，重新开始
                onUserSuccess(new ResBase());
            }
        };

        Disposable subscribe = lFlowable.subscribeWith(subscriber);
        mRxManage.add(subscribe);
    }

    private void registerToWeb(String cookie) {
        WebViewActivity.startActivity(mContext, "https://www.baidu.com", WebViewActivity.TYPE_WEB, cookie);
    }


    public abstract Flowable<ResBase> registerFlowable(String phone, String code, String piccode);

    protected void setRegistCountDownTime(final ResBase resBase, final int registInterval) {
        DisposableSubscriber<Long> subscriber = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(Long aLong) {
                mViewRef.get().changeHuLianState(resBase.msg + "," + TimeUtils.DateFormat2MS(aLong) + "后重新开始注册");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                getmobile();
            }
        };

        Disposable disposable = Flowable.interval(1, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .take(registInterval)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return (registInterval - aLong) * 1000;
                    }
                })
                .compose(RxUtils.<Long>rxScheduler())
                .subscribeWith(subscriber);

        mRxManage.add(disposable);


    }

    private int smsFailCount = 0;


    public void getSms(final long time, final String mobile) {
        getSms(time, mobile, "");
    }


    public void getSms(final long time, final String mobile, final String piccode) {

        Flowable<Object> lFlowable = Flowable.timer(time, TimeUnit.SECONDS)
                .flatMap(new Function<Long, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Long aLong) throws Exception {
                        return YiMaDataCenter.getInstance().getSms(mobile)
                                .compose(RxUtils.<ResponseBody>getScheduler(true, getView()));
                    }
                }).retryWhen(new RetryWhenFunction());


        BaseSubscriber baseSubscriber = new BaseSubscriber<ResponseBody>() {
            @Override
            protected void onUserSuccess(ResponseBody responseBody) {
                //忽略
                addIgnore(mobile);

                String text = responseBody.source().toString();
                int start = 0;
                int end = start + 4;
                //先判断连续4个数字的位置
                while (!isNumericzidai(text.substring(start, end))) {
                    start++;
                    end++;
                }
                while (isNumericzidai(text.substring(start, end + 1))) {
                    end++;
                }
                String code = text.substring(start, end);
                mViewRef.get().changeYiMaState(mobile + "获取验证码成功" + code);
                regist(mobile, code, piccode);
            }

            @Override
            protected void onUserFail(ResponseBody responseBody) {
                //获取10次失败，则换一个号码
                if (++smsFailCount > 10) {
                    smsFailCount = 0;
                    getmobile();
                } else {
                    getSms(6, mobile, piccode);
                }
                LogUtils.e(mobile + "失败" + smsFailCount + "次, msg:" + responseBody.source().toString());
            }

        };

        Disposable disposable = lFlowable.subscribeWith(baseSubscriber);
        mRxManage.add(disposable);
        setSmsCountDownTime(time);
    }


    public static boolean isNumericzidai(String str) {
        String regEx = "^-?[0-9]+$";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }

    private void setSmsCountDownTime(final long time) {
        Consumer<? super Long> onNext = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                mViewRef.get().changeYiMaState(TimeUtils.DateFormat2MS(aLong) + "后获取验证码");
            }
        };

        Disposable subscribe = Flowable.interval(1, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .take(time)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return (time - aLong) * 1000;
                    }
                })
                .compose(RxUtils.<Long>rxScheduler())
                .subscribe(onNext, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

        mRxManage.add(subscribe);
    }


    public void getmobile() {
        getmobile(0);
    }

    public void getmobile(final int time) {
        smsFailCount = 0;
        Flowable<Object> lFlowable = Flowable.timer(time, TimeUnit.SECONDS)
                .flatMap(new Function<Long, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Long aLong) throws Exception {
                        while (judgeLimite()) {
                            int time = 30;
                            Disposable subscribe = Flowable
                                    .interval(1, TimeUnit.SECONDS)
                                    .onBackpressureDrop()
                                    .compose(RxUtils.<Long>rxScheduler())
                                    .take(time)
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            mViewRef.get().changeYiMaState("limite:" + aLong);
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            throwable.printStackTrace();
                                        }
                                    });
                            mRxManage.add(subscribe);
                        }
                        return YiMaDataCenter.getInstance().getmobile()
                                .compose(RxUtils.<ResponseBody>getScheduler(true, getView()));
                    }
                }).retryWhen(new RetryWhenFunction());

        BaseSubscriber baseSubscriber = new BaseSubscriber<ResponseBody>() {
            @Override
            protected void onUserSuccess(ResponseBody responseBody) {

                String text = responseBody.source().toString();
                String mobile = text.substring(text.indexOf("|") + 1, text.lastIndexOf("]"));
                mViewRef.get().changeYiMaState("获取号码成功" + mobile);
                String smsType = BaseApplication.getContext().getString(R.string.sms_type);
                String captchaType = BaseApplication.getContext().getString(R.string.captcha_type);

                if (captchaType.equals(GlobalConstant.CAPTCHA_NO)) {
                    if (smsType.equals(GlobalConstant.SMS_HAD)) {
                        reqSms(mobile);
                    } else {
                        regist(mobile);
                    }
                } else if (captchaType.equals(GlobalConstant.CAPTCHA_MANUAL)) {
                    ClipboardUtils.copyText(mobile);
                    goManualWeb();
                    //手动请求短信，直接获取短信
                    getSms(16, mobile);
                }

            }

            @Override
            protected void onUserFail(ResponseBody responseBody) {
                super.onUserFail(responseBody);
                String text = responseBody.source().toString();
                if (text.contains("1008")) {
                    text = "余额不足";
                } else if (text.contains("1004")) {
                    text = "token失效";
                } else if (text.contains("1009")) {
                    text = "账户被禁用";
                } else if (text.contains("2002")) {
                    text = "项目不存在";
                } else if (text.contains("2003")) {
                    text = "项目未启用";
                } else {
                    if (text.contains("2005")) {
                        text = "获取号码数量已达到上限";
                    } else if (text.contains("9003")) {
                        text = "系统繁忙";
                    } else if (text.contains("1012")) {
                        text = "登录数达到上限";
                    }
                    getmobile(15);
                }
                mViewRef.get().changeHuLianState("获取号码失败" + text);
            }
        };

        Disposable disposable = lFlowable.subscribeWith(baseSubscriber);
        mRxManage.add(disposable);
    }


    /**
     * 手动操作
     */
    protected void goManualWeb() {
        WebViewActivity.startActivity(mContext, "https://www.baidu.com", WebViewActivity.TYPE_MANUAL);
    }



    public void addIgnore(String mobile) {
        Disposable subscribe = YiMaDataCenter.getInstance().addIgnore(mobile)
                .compose(RxUtils.<ResponseBody>getScheduler(true, getView()))
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        mRxManage.add(subscribe);
    }


    /**
     * 判断是否需要短信
     *
     * @param captcha
     * @param mobile
     */
    private void judgeSmsNeed(String captcha, String mobile) {
        String smsType = BaseApplication.getContext().getString(R.string.sms_type);
        if (smsType.equals(GlobalConstant.SMS_HAD)) {
            reqSms(mobile, captcha);
        } else {
            regist(mobile, "", captcha);
        }
    }

    public abstract Flowable<ResponseBody> getCaptchaFlowable();

    /**
     * 刷新token
     */
    public class RetryWhenFunction implements Function<Flowable<Throwable>, Publisher<?>> {
        @Override
        public Publisher<?> apply(Flowable<Throwable> throwableFlowable) {
            return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
                @Override
                public Publisher<?> apply(Throwable throwable) {
                    //网络错误,延迟3秒后重新请求
                    if (throwable instanceof HttpException
                            || throwable instanceof ConnectException
                            || throwable instanceof SocketTimeoutException
                            || throwable instanceof SocketException || throwable instanceof UnknownHostException) {
                        ToastUtils.showShort("network fail");
                        return Flowable.timer(3, TimeUnit.SECONDS);
                    }
                    return Flowable.error(throwable);
                }
            });
        }
    }

    /**
     * 判断限制，时间和ip限制
     *
     * @return
     */
    protected boolean judgeLimite() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //正式环境限制，测试环境不限制
        if (BuildConfig.BUILD_TYPE.equals("release")) {
//            String v4IP = IPUtils.getV4IP();
//            LogUtils.e("ip" + v4IP);
//            if (v4IP.equals("113.87.89.206")) {
//                return true;
//            }
            if (!setNotLimiteTime(currentHour)) {
                return true;
            }
        }
        return false;
    }

    protected boolean setNotLimiteTime(int currentHour) {
        if (currentHour >= 9 && currentHour < 20) {
            return true;
        }
        return false;
    }


}
