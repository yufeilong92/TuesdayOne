package com.xuechuan.xcedu.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.xuechuan.xcedu.mvp.contract.CollectSubmitContract;
import com.xuechuan.xcedu.mvp.model.CollectSubmitModel;
import com.xuechuan.xcedu.mvp.model.ErrorSubmtModel;
import com.xuechuan.xcedu.mvp.presenter.CollectSubmitPresenter;
import com.xuechuan.xcedu.mvp.presenter.ErrorSubmtPresenter;
import com.xuechuan.xcedu.sqlitedb.SubmiteLogHelp;
import com.xuechuan.xcedu.sqlitedb.UpCollectSqlteHelp;
import com.xuechuan.xcedu.sqlitedb.UpDeleteCollectSqlteHelp;
import com.xuechuan.xcedu.sqlitedb.UpDeleteErrorSqlteHelp;
import com.xuechuan.xcedu.sqlitedb.UpErrorSqlteHelp;
import com.xuechuan.xcedu.utils.TimeUtil;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.ResultVo;
import com.xuechuan.xcedu.vo.SqliteVo.CollectSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.ErrorSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.SubmitLogVo;

import java.util.ArrayList;
import java.util.Date;

/**
 * @Title:  SubmitCollectService
 * @Package com.xuechuan.xcedu.service
 * @Description: 收藏
 * @author: L-BackPacker
 * @date:   2019.01.08 下午 3:44
 * @version V 1.0 xxxxxxxx
 * @verdescript  版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2019.01.08
 */

public class SubmitCollectService extends IntentService implements CollectSubmitContract.View {
    private static final String ACTION_FOO = "com.xuechuan.xcedu.service.action.FOO";

    private static final String EXTRA_PARAM1 = "com.xuechuan.xcedu.service.extra.PARAM1";
    private int mSaffid;
    private UpCollectSqlteHelp mUpCollectSqlteHelp;
    private UpDeleteCollectSqlteHelp mUpDeleteCollectSqlteHelp;
    private ArrayList<CollectSqliteVo> mCollectSqliteVoLists;
    private ArrayList<CollectSqliteVo> mCollectSqliteVo1Lists;

    public SubmitCollectService() {
        super("SubmitCollectService");
    }


    public static void startActionFoo(Context context, int  saffid) {
        Intent intent = new Intent(context, SubmitCollectService.class);
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
        mUpCollectSqlteHelp = UpCollectSqlteHelp.getInstance(this);
        mUpDeleteCollectSqlteHelp = UpDeleteCollectSqlteHelp.getInstance(this);
        mCollectSqliteVoLists = mUpCollectSqlteHelp.queryAllList();
        mCollectSqliteVo1Lists = mUpDeleteCollectSqlteHelp.queryAllList();
        if (mCollectSqliteVoLists == null && mCollectSqliteVo1Lists == null) {
            stopSelf();
            return;
        }
        if (mCollectSqliteVoLists.isEmpty() && mCollectSqliteVo1Lists.isEmpty()) {
            stopSelf();
            return;
        }
        CollectSubmitPresenter presenter = new CollectSubmitPresenter();
        presenter.initModelView(new CollectSubmitModel(), this);
        StringBuffer collectDo = new StringBuffer();
        if (!mCollectSqliteVoLists.isEmpty()) {
            for (int i = 0; i < mCollectSqliteVoLists.size(); i++) {
               CollectSqliteVo vo = mCollectSqliteVoLists.get(i);
                if (i == mCollectSqliteVoLists.size() - 1) {
                    collectDo.append(vo.getQuestion_id());
                } else {
                    collectDo.append(vo.getQuestion_id());
                    collectDo.append(" ");
                }
            }
        }
        StringBuffer collectDos = new StringBuffer();
        if (!mCollectSqliteVo1Lists.isEmpty()) {
            for (int i = 0; i < mCollectSqliteVo1Lists.size(); i++) {
                CollectSqliteVo vo = mCollectSqliteVo1Lists.get(i);
                if (i == mCollectSqliteVo1Lists.size() - 1) {
                    collectDos.append(vo.getQuestion_id());
                } else {
                    collectDos.append(vo.getQuestion_id());
                    collectDos.append(" ");
                }
            }
        }

        presenter.submitCollect(this,collectDo.toString(),collectDos.toString());

    }


    @Override
    public void submitCollectSuccess(String success) {
        ResultVo vo = Utils.getGosnT(success, ResultVo.class);
        if (vo.getStatus().getCode()==200){
            if (vo.getData().getStatusX()==1){
                SubmiteLogHelp mSubmiteLogHelp = SubmiteLogHelp.get_Instance(this);
                SubmitLogVo vo2 = new SubmitLogVo();
                vo2.setSaffid(mSaffid);
                vo2.setCollecttime(TimeUtil.dateToString(new Date()));
                mSubmiteLogHelp.addItem(vo2);
                if (mCollectSqliteVoLists!=null&&!mCollectSqliteVoLists.isEmpty()){
                    for (int i = 0; i < mCollectSqliteVoLists.size(); i++) {
                        CollectSqliteVo sqliteVo = mCollectSqliteVoLists.get(i);
                        mUpCollectSqlteHelp.delectItem(sqliteVo.getId());
                    }
                }
                if (mCollectSqliteVo1Lists!=null&&!mCollectSqliteVo1Lists.isEmpty()){
                    for (int i = 0; i < mCollectSqliteVo1Lists.size(); i++) {
                        CollectSqliteVo sqliteVo = mCollectSqliteVo1Lists.get(i);
                        mUpDeleteCollectSqlteHelp.delectItem(sqliteVo.getId());
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
    public void submitCollectError(String msg) {
        handleActionFoo();
    }
}
