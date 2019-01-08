package com.xuechuan.xcedu.live;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.base.BaseActivity;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: LiveInfomActivity
 * @Package com.xuechuan.xcedu.live
 * @Description: 直播课详情信息页
 * @author: L-BackPacker
 * @date: 2018.10.13 上午 10:04
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018.10.13
 */

public class LiveInfomActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvLiveProgress;
    private WebView mWebLiveContent;
    private TextView mTvLivePrice;
    private TextView mTvLiveService;
    private Button mBtnLiveBuy;
    private LinearLayout mLlLiveButtomFloat;
    private Button mBtnLiveValue;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);w
        setContentView(R.layout.activity_live_infom);
        initView();
    }
*/
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_infom);
        initView();
    }

    private void initView() {
        mIvLiveProgress = (ImageView) findViewById(R.id.iv_live_progress);
        mWebLiveContent = (WebView) findViewById(R.id.web_live_content);
        mTvLivePrice = (TextView) findViewById(R.id.tv_live_price);
        mTvLiveService = (TextView) findViewById(R.id.tv_live_service);
        mBtnLiveBuy = (Button) findViewById(R.id.btn_live_buy);
        mLlLiveButtomFloat = (LinearLayout) findViewById(R.id.ll_live_buttom_float);
        mBtnLiveValue = (Button) findViewById(R.id.btn_live_value);

        mBtnLiveBuy.setOnClickListener(this);
        mBtnLiveValue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_live_buy:

                break;
            case R.id.btn_live_value:

                break;
        }
    }
}
