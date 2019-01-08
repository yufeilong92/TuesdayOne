package com.xuechuan.xcedu.fragment.home;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.adapter.home.MyTagPagerAdapter;
import com.xuechuan.xcedu.adapter.me.MyOrderIndicatorAdapter;
import com.xuechuan.xcedu.base.BaseFragment;
import com.xuechuan.xcedu.fragment.net.NetCatalogueTabFragment;
import com.xuechuan.xcedu.fragment.net.NetEvaluateFragment;
import com.xuechuan.xcedu.fragment.net.NetInfomTabFragment;
import com.xuechuan.xcedu.utils.ArrayToListUtil;
import com.xuechuan.xcedu.vo.HomeInfomBean;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.fragment
 * @Description: 问答
 * @author: L-BackPacker
 * @date: 2018/7/10 16:08
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class AnswerFragment extends BaseFragment {

    private Context mContext;

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer, container, false);
        initView(view);
        return view;
    }
*/

    private void initView(View view) {
        mContext = getActivity();

    }

    @Override
    protected int initInflateView() {
        return R.layout.fragment_answer;
    }

    @Override
    protected void initCreateView(View view, Bundle savedInstanceState) {
        initView(view);
        initData();

    }

    private void initData() {
    }



}
