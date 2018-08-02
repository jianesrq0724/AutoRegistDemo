package com.carl.mvpdemo.module.home.view;

import android.view.View;
import android.widget.TextView;

import com.carl.mvpdemo.R;
import com.carl.mvpdemo.module.home.interfaces.MainI;
import com.carl.mvpdemo.module.home.presenter.MainPresenter;
import com.carl.mvpdemo.pub.base.BaseListActivity;
import com.carl.mvpdemo.pub.base.CommonBaseAdapter;
import com.carl.mvpdemo.pub.base.CommonSimpleAdapter;
import com.carl.mvpdemo.pub.utils.LogUtils;
import com.carl.mvpdemo.pub.utils.SPKeyUtils;
import com.carl.mvpdemo.pub.utils.SPUtils;
import com.carl.mvpdemo.pub.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carl
 * @version 1.0
 * @since 2018/5/10
 */
public class MainBaseActivity extends BaseListActivity<MainI, MainPresenter> implements MainI {

    private List<String> mTitles = new ArrayList<>();

    private TextView mSuccessTv;
    private TextView mFailTv;
    private TextView mYiMaStateTv;
    private TextView mHuLianStateTv;

    private void assignViews() {
        mSuccessTv = findViewById(R.id.success_tv);
        mFailTv = findViewById(R.id.fail_tv);
        mYiMaStateTv = findViewById(R.id.yima_state_tv);
        mHuLianStateTv = findViewById(R.id.hulian_state_tv);
    }

    @Override
    public void setOnInteractListener() {

    }

    @Override
    protected void findView() {
        super.findView();
        assignViews();
    }

    @Override
    protected void initData() {
        mTitles.add("getmobile");
        mTitles.add("reqsms");
        mPresenter.getmobile();
    }

    @Override
    protected void initView() {
        mToolbarManager.hideBackIcon();
        mToolbarManager.setToolbarTitle(getString(R.string.app_name));
        refreshCount();

        mBaseAdapter = new CommonSimpleAdapter(mTitles);
        initListView();

        mBaseAdapter.setOnItemClickListener(new CommonBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        //清除所求请求
                        mPresenter.mRxManage.clear();
                        mPresenter.getmobile();
                        break;
                    case 1:
                        mPresenter.reqSms("15526134626");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void getFirstData() {

    }

    @Override
    protected void onLoad() {

    }

    private long lastClickTime;

    @Override
    public void onBackPressed() {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 2000) {
            ToastUtils.showShort("再按一次退出");
            lastClickTime = currentClickTime;
        } else {
            super.onBackPressed();
        }
    }

    private void setSuccessTV() {
        int registSuccess = SPUtils.getInstance().getInt(SPKeyUtils.REGIST_SUCCESS);
        mSuccessTv.setText(getString(R.string.regist_success, registSuccess));
    }

    private void setFailCount() {
        int registFail = SPUtils.getInstance().getInt(SPKeyUtils.REGIST_FAIL);
        mFailTv.setText(getString(R.string.regist_fail, registFail));
    }

    @Override
    public void refreshCount() {
        setSuccessTV();
        setFailCount();
    }

    @Override
    public void changeHuLianState(String text) {
        mHuLianStateTv.setText(getString(R.string.current_state, text));
        LogUtils.e(text);
    }

    @Override
    public void changeYiMaState(String text) {
        mYiMaStateTv.setText(getString(R.string.current_state, text));
        LogUtils.e(text);
    }

}
