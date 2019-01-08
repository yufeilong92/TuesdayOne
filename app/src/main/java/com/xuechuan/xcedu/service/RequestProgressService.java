package com.xuechuan.xcedu.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
/**
 * @Title:  RequestProgressService
 * @Package com.xuechuan.xcedu.mvp.presenter
 * @Description: 获取进度
 * @author: L-BackPacker
 * @date:   2019.01.08 下午 6:53
 * @version V 1.0 xxxxxxxx
 * @verdescript  版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2019.01.08
 */
public class RequestProgressService extends IntentService {
    private static final String ACTION_FOO = "com.xuechuan.xcedu.mvp.presenter.action.FOO";

    private static final String EXTRA_PARAM1 = "com.xuechuan.xcedu.mvp.presenter.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.xuechuan.xcedu.mvp.presenter.extra.PARAM2";
    private int mSaffid;

    public RequestProgressService() {
        super("RequestProgressService");
    }

    public static void startActionFoo(Context context, int saffid) {
        Intent intent = new Intent(context, RequestProgressService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, saffid);
        context.startService(intent);
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                mSaffid = intent.getIntExtra(EXTRA_PARAM1,0);
                handleActionFoo();
            }
        }
    }

    private void handleActionFoo() {
    }


}
