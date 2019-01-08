package com.xuechuan.xcedu.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.net.view.StringCallBackUpView;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.ui.LoginActivity;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.SaveUUidUtil;
import com.xuechuan.xcedu.utils.StringSort;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.GetParamVo;
import com.xuechuan.xcedu.vo.HttpInfomVo;
import com.xuechuan.xcedu.vo.UserBean;
import com.xuechuan.xcedu.vo.UserInfomVo;

import org.json.JSONObject;

import java.io.File;
import java.io.FilePermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/5/28 15:26
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class UpDataService {

    public void upImgHear(Context context, List<String> listPath, final String url, final JsonObject params,
                          boolean isWithToken, final StringCallBackUpView callBackView) {
        UserBean user = null;
        if (isWithToken) {
            UserInfomVo vo = MyAppliction.getInstance().getUserInfom();
            if (vo == null) {
                MyAppliction.getInstance().startLogin(context);
                T.showToast(context, context.getString(R.string.please_login));
                return;
            }
            user = vo.getData().getUser();
        }
        addParams(context, params);
        MediaType parse = MediaType.parse(DataMessageVo.HTTPAPPLICAITON);
        HttpInfomVo infomVo = getInfomData();

        String signature = null;
        if (isWithToken) {
            infomVo.setStaffid(String.valueOf(user.getId()));
            infomVo.setToken(user.getToken());
            StringSort sort = new StringSort();
            signature = sort.getOrderMd5Data(params);
        } else {
            infomVo.setToken(null);
            infomVo.setStaffid(null);
        }
        RequestBody requestBody = RequestBody.create(parse,
                params.toString());

        sendRequestPostHttp(context, listPath, url, infomVo.getStaffid(), infomVo.getTimeStamp(), infomVo.getNonce()
                , signature, requestBody, callBackView);
    }

    protected void sendRequestPostHttp(final Context context, List<String> listPath, String url, String saffid,
                                       String time, String nonce, String signature,
                                       RequestBody requestBody, final StringCallBackUpView callBackView) {
        if (StringUtil.isEmpty(saffid))

        {
            saffid = "0";
        }

        String hear = context.getResources().getString(R.string.app_content_heat);
        url = hear.concat(url);
        PostRequest<String> request = OkGo.<String>post(url)
                .tag(context)
                .isMultipart(true)
                .headers(DataMessageVo.STAFFID, StringUtil.isEmpty(saffid) ? null : saffid)
                .headers(DataMessageVo.TIMESTAMP, StringUtil.isEmpty(time) ? null : time)
                .headers(DataMessageVo.NONCE, StringUtil.isEmpty(nonce) ? null : nonce)
                .headers(DataMessageVo.SIGNATURE, StringUtil.isEmpty(signature) ? null : signature)
//                .upRequestBody(requestBody);
                .params("staffid", saffid);
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            files.add(new File(listPath.get(i)));
        }
        request.addFileParams("image", files);

        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    new JsonParser().parse(response.body().toString());
                    OkGo.getInstance().cancelTag(context);
                    callBackView.onSuccess(response);
                } catch (JsonParseException e) {
                    L.e("数据异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                L.e(response.message());
                callBackView.onError(response);
            }

            @Override
            public void uploadProgress(Progress progress) {
                Log.e("图片上传====", "uploadProgress: " + progress);
                callBackView.onUpProgree(progress);
            }
        });

    }

    /**
     * 添加参数
     *
     * @param context
     * @param param
     */
    private void addParams(Context context, JsonObject param) {
        String newType = Utils.getNewType(context);
        param.addProperty(DataMessageVo.HTTP_AC, newType);
        String versionName = Utils.getVersionName(context);
        param.addProperty(DataMessageVo.HTTP_VERSION_NAME, versionName);
        int code = Utils.getVersionCode(context);
        param.addProperty(DataMessageVo.HTTP_VERSION_CODE, String.valueOf(code));
        param.addProperty(DataMessageVo.HTTP_DEVICE_PLATFORM, "android");
        String model = Utils.getSystemModel();
        param.addProperty(DataMessageVo.HTTP_DEVICE_TYPE, model);
        String brand = Utils.getDeviceBrand();
        param.addProperty(DataMessageVo.HTTP_DEVICE_BRAND, brand);
        String systemVersion = Utils.getSystemVersion();
        param.addProperty(DataMessageVo.HTTP_OS_VERSION, systemVersion);
        String dp = Utils.getdp(context);
        param.addProperty(DataMessageVo.HTTP_RESOLUTION, dp);
        String dpi = Utils.getdpi(context);
        param.addProperty(DataMessageVo.HTTP_DPI, dpi);
    }

    public HttpInfomVo getInfomData() {
        HttpInfomVo infomVo = MyAppliction.getInstance().getHttpInfomInstance();
        String time = String.valueOf(new Date().getTime());
        String random8 = String.valueOf(Utils.getRandom8());
        infomVo.setTimeStamp(String.valueOf(time));
        infomVo.setNonce(String.valueOf(random8));
        return infomVo;
    }

}
