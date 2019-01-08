package com.xuechuan.xcedu.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.xuechuan.xcedu.mvp.contract.ErrorSubmtContract;
import com.xuechuan.xcedu.mvp.model.ErrorSubmtModel;
import com.xuechuan.xcedu.mvp.presenter.ErrorSubmtPresenter;
import com.xuechuan.xcedu.sqlitedb.SubmiteLogHelp;
import com.xuechuan.xcedu.sqlitedb.UpDeleteErrorSqlteHelp;
import com.xuechuan.xcedu.sqlitedb.UpErrorSqlteHelp;
import com.xuechuan.xcedu.utils.TimeUtil;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.ResultVo;
import com.xuechuan.xcedu.vo.SqliteVo.ErrorSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.SubmitLogVo;
import com.xuechuan.xcedu.vo.UpdataOrDeleteVo;

import java.util.ArrayList;
import java.util.Date;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: SubmitErrorService
 * @Package com.xuechuan.xcedu.service
 * @Description: 提交错题
 * @author: L-BackPacker
 * @date: 2019.01.08 下午 2:28
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2019.01.08
 */
public class SubmitErrorService extends IntentService implements ErrorSubmtContract.View {
    private static final String ACTION_FOO = "com.xuechuan.xcedu.service.action.FOO";

    private static final String EXTRA_PARAM1 = "com.xuechuan.xcedu.service.extra.PARAM1";
    private int mSaffid;
    private ArrayList<ErrorSqliteVo> mErrorSqliteVoLists;
    private ArrayList<ErrorSqliteVo> mErrorSqliteVo1Lists;
    private UpErrorSqlteHelp mErrorSqlteHelp;
    private UpDeleteErrorSqlteHelp mDeleteErrorSqlteHelp;

    public SubmitErrorService() {
        super("SubmitErrorService");
    }

    public static void startActionFoo(Context context, int  saffid) {
        Intent intent = new Intent(context, SubmitErrorService.class);
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
        mErrorSqlteHelp = UpErrorSqlteHelp.getInstance(this);
        mDeleteErrorSqlteHelp = UpDeleteErrorSqlteHelp.getInstance(this);
        mErrorSqliteVoLists = mErrorSqlteHelp.queryAllList();
        mErrorSqliteVo1Lists = mDeleteErrorSqlteHelp.queryAllList();
        if (mErrorSqliteVoLists == null && mErrorSqliteVo1Lists == null) {
            stopSelf();
            return;
        }
        if (mErrorSqliteVoLists.isEmpty() && mErrorSqliteVo1Lists.isEmpty()) {
            stopSelf();
            return;
        }
        ErrorSubmtPresenter presenter = new ErrorSubmtPresenter();
        presenter.initModelView(new ErrorSubmtModel(), this);
        StringBuffer errorDe = new StringBuffer();
        if (!mErrorSqliteVoLists.isEmpty()) {
            for (int i = 0; i < mErrorSqliteVoLists.size(); i++) {
                ErrorSqliteVo vo = mErrorSqliteVoLists.get(i);
                if (i == mErrorSqliteVoLists.size() - 1) {
                    errorDe.append(vo.getQuesitonid());
                } else {
                    errorDe.append(vo.getQuesitonid());
                    errorDe.append(" ");
                }
            }
        }
        StringBuffer errorDelete = new StringBuffer();
        if (!mErrorSqliteVo1Lists.isEmpty()) {
            for (int i = 0; i < mErrorSqliteVo1Lists.size(); i++) {
                ErrorSqliteVo vo = mErrorSqliteVo1Lists.get(i);
                if (i == mErrorSqliteVo1Lists.size() - 1) {
                    errorDelete.append(vo.getQuesitonid());
                } else {
                    errorDelete.append(vo.getQuesitonid());
                    errorDelete.append(" ");
                }
            }
        }
//        Log.e("===", "handleActionFoo: "+errorDe.toString()+"\n"+errorDelete.toString() );
        presenter.submitError(this,errorDe.toString(),errorDelete.toString());
    }


    @Override
    public void submitErrorSuccess(String success) {
        ResultVo vo = Utils.getGosnT(success, ResultVo.class);
        if (vo.getStatus().getCode()==200){
            if (vo.getData().getStatusX()==1){
                SubmiteLogHelp mSubmiteLogHelp = SubmiteLogHelp.get_Instance(this);
                SubmitLogVo vo2 = new SubmitLogVo();
                vo2.setSaffid(mSaffid);
                vo2.setErrortime(TimeUtil.dateToString(new Date()));
                mSubmiteLogHelp.addItem(vo2);
                if (mErrorSqliteVoLists!=null&&!mErrorSqliteVoLists.isEmpty()){
                    for (int i = 0; i < mErrorSqliteVoLists.size(); i++) {
                        ErrorSqliteVo sqliteVo = mErrorSqliteVoLists.get(i);
                        mErrorSqlteHelp.delectErrorItem(sqliteVo.getId());
                    }
                }
                if (mErrorSqliteVo1Lists!=null&&!mErrorSqliteVo1Lists.isEmpty()){
                    for (int i = 0; i < mErrorSqliteVo1Lists.size(); i++) {
                        ErrorSqliteVo sqliteVo = mErrorSqliteVo1Lists.get(i);
                        mDeleteErrorSqlteHelp.delectErrorItem(sqliteVo.getId());
                    }
                }
                stopSelf();
            }else {
                handleActionFoo();
            }
        }else {
            handleActionFoo();
        }

    }

    @Override
    public void submitErrorError(String msg) {
        handleActionFoo();
    }
}
