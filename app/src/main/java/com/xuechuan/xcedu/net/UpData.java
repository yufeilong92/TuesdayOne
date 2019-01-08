package com.xuechuan.xcedu.net;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.net.view.StringCallBackUpView;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.ui.LoginActivity;
import com.xuechuan.xcedu.utils.SaveUUidUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.vo.UserBean;
import com.xuechuan.xcedu.vo.UserInfomVo;

import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/5/28 15:40
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class UpData extends UpDataService {
    private Context mContext;
    private static UpData service;

    public UpData(Context mContext) {
        this.mContext = mContext;
    }

    public static UpData getInstance(Context context) {
        if (service == null) {
            service = new UpData(context);
        }
        return service;
    }

    /**
     * 修改用户头像接口
     *
     * @param view
     */
    public void submitchangeheadimg(List<String >listPath, StringCallBackUpView view) {

        UserInfomVo userInfom = MyAppliction.getInstance().getUserInfom();
        if (userInfom == null) {
            MyAppliction.getInstance().startLogin(mContext);
            T.showToast(mContext, mContext.getResources().getString(R.string.please_login));
            return;
        }
        UserBean user = userInfom.getData().getUser();
        JsonObject object = new JsonObject();
        object.addProperty("staffid", user.getId());
        String url = mContext.getResources().getString( R.string.http_m_hear);
        upImgHear(mContext,listPath, url, object, true, view);
    }

}
