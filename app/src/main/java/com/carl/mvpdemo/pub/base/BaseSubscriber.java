package com.carl.mvpdemo.pub.base;

import android.text.TextUtils;

import com.carl.mvpdemo.pub.network.bean.ResBase;
import com.carl.mvpdemo.pub.utils.ToastUtils;

import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.ResponseBody;

/**
 * @author Carl
 * version 1.0
 * @since 2018/6/11
 */
public abstract class BaseSubscriber<T> extends DisposableSubscriber<T> {

    @Override
    public void onNext(T t) {

        if (t instanceof ResponseBody) {
            String s = ((ResponseBody) t).source().toString();
            if (s.contains("success")) {
                onUserSuccess(t);
            } else {
                onUserFail(t);
            }
        } else if (t instanceof ResBase) {
            String code = ((ResBase) t).code;
            String success = ((ResBase) t).success;
            String status = ((ResBase) t).Status;
            String status2 = ((ResBase) t).status;

            if (judgeCode(code) || judgeSuccess(success) || judgeStatus(status) || judgeCode(status2)) {
                onUserSuccess(t);
            } else {
                onUserFail(t);
            }
        }
    }

    private boolean judgeStatus(String status) {
        if (TextUtils.isEmpty(status)) {
            return false;
        }
        if (status.equals("true") || status.equals("200")) {
            return true;
        } else {
            return false;
        }
    }


    private boolean judgeSuccess(String success) {
        if (TextUtils.isEmpty(success)) {
            return false;
        }
        if (success.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean judgeCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }

        if (code.equals("200") || code.equals("1") || code.equals("5") || code.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onError(Throwable e) {
        onUserError(e);
    }

    @Override
    public void onComplete() {

    }

    protected abstract void onUserSuccess(T t);

    protected void onUserFail(T t) {
        ToastUtils.showLong("onUserFail");
    }

    protected void onUserError(Throwable t) {
        ToastUtils.showLong(t.getMessage());
    }
}
