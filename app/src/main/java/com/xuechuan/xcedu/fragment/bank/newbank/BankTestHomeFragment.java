package com.xuechuan.xcedu.fragment.bank.newbank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.PolyvDevMountInfo;
import com.lzy.okgo.model.Progress;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.base.BaseFragment;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.event.DayHomeEvent;
import com.xuechuan.xcedu.mvp.contract.BankHomeGradeContract;
import com.xuechuan.xcedu.mvp.contract.BuyBankContract;
import com.xuechuan.xcedu.mvp.contract.DayExerciseContract;
import com.xuechuan.xcedu.mvp.contract.FileDownContract;
import com.xuechuan.xcedu.mvp.model.BankHomeGradeModel;
import com.xuechuan.xcedu.mvp.model.BuyBankModel;
import com.xuechuan.xcedu.mvp.model.DayExerciseModel;
import com.xuechuan.xcedu.mvp.model.FileDownModel;
import com.xuechuan.xcedu.mvp.presenter.BankHomeGradePresenter;
import com.xuechuan.xcedu.mvp.presenter.BuyBankPresenter;
import com.xuechuan.xcedu.mvp.presenter.DayExercisePresenter;
import com.xuechuan.xcedu.mvp.presenter.FileDownPresenter;

import com.xuechuan.xcedu.sqlitedb.SqliteHelp;
import com.xuechuan.xcedu.sqlitedb.UserInfomDbHelp;
import com.xuechuan.xcedu.utils.CompressOperate_zip4j;
import com.xuechuan.xcedu.utils.DialogUtil;
import com.xuechuan.xcedu.utils.EncryptionUtil;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.BankGradeVo;
import com.xuechuan.xcedu.vo.BuyVo;
import com.xuechuan.xcedu.vo.DayHomeBeanVo;
import com.xuechuan.xcedu.vo.DayHomeVo;
import com.xuechuan.xcedu.vo.NoteListVo;
import com.xuechuan.xcedu.vo.SqliteVo.UserInfomSqliteVo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: BankTestFragment
 * @Package com.xuechuan.xcedu.fragment.bank
 * @Description: 新的题库界面
 * @author: L-BackPacker
 * @date: 2018.12.04 下午 4:02
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018.12.04
 */
public class BankTestHomeFragment extends BaseFragment implements View.OnClickListener, FileDownContract.View, BankHomeGradeContract.View, BuyBankContract.View, DayExerciseContract.View {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context mContext;
    private TextView mTvBanktestSkill;
    private TextView mTvBanktextColligate;
    private TextView mTvBanktestCaseNew;
    private FrameLayout mFlContent;

    private int FragmentLayout = R.id.fl_banktest_content;
    private ArrayList<Fragment> fragments;
    private FileDownPresenter mPresenter;

    private static String TAG = "【" + BankTestHomeFragment.class + "】==";
    private DialogUtil mUpdataUtil;
    private BankHomeGradePresenter mBankGradePresenter;


    public static BankTestHomeFragment newInstance(String param1, String param2) {
        BankTestHomeFragment fragment = new BankTestHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_test_home, container, false);
        initView(view);
        return view;
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int initInflateView() {
        return R.layout.fragment_bank_test_home;
    }

    @Override
    protected void initCreateView(View view, Bundle savedInstanceState) {
        initView(view);
        initRequest();
        initData();
        fragments = creartFragment();

    }

    private void initData() {
        BuyBankPresenter presenter = new BuyBankPresenter();
        presenter.initModelView(new BuyBankModel(), this);
        presenter.reqeusstUserBuy(mContext);
        DayExercisePresenter dayExercisePresenter = new DayExercisePresenter();
        dayExercisePresenter.initModelView(new DayExerciseModel(), this);
        dayExercisePresenter.requestiDayExercise(mContext);
        dayExercisePresenter.requestNoteCount(mContext);
    }

    private void initRequest() {
        mBankGradePresenter = new BankHomeGradePresenter();
        mBankGradePresenter.initModelView(new BankHomeGradeModel(), this);
        mBankGradePresenter.reuqestGrade(mContext, "0");

    }

    private void initShowUpdataData() {
        mPresenter = new FileDownPresenter();
        mPresenter.initModelView(new FileDownModel(), this);
        mUpdataUtil = DialogUtil.getInstance();
        mUpdataUtil.showBankUpDialogOne(mContext, true);
        sendRequestBank();
/*        mUpdataUtil = DialogUtil.getInstance();
        mUpdataUtil.showBankUpDialog(mContext, false, "测试内容", new DialogUtil.OnBankClickListener() {
            @Override
            public void onCancelClickListener() {
            }

            @Override
            public void onUpdataClickListener() {
                sendRequestBank();
            }
        });*/
    }

    /**
     * 下载字典库
     */
    private void sendRequestBank() {
        File saveDir = new File(PolyvDevMountInfo.getInstance().getInternalSDCardPath() + File.separator + "xuechuan");
        String hear = getStrWithId(R.string.app_content_heat);
        String concat = hear.concat(getStrWithId(R.string.http_app_bank_file_down));
        mPresenter.fileDownRequest(mContext, concat, saveDir.getAbsolutePath());
    }


    private void initView(View view) {
        mContext = getActivity();
        mTvBanktestSkill = (TextView) view.findViewById(R.id.tv_banktest_skill);
        mTvBanktestSkill.setOnClickListener(this);
        mTvBanktextColligate = (TextView) view.findViewById(R.id.tv_banktext_colligate_);
        mTvBanktextColligate.setOnClickListener(this);
        mTvBanktestCaseNew = (TextView) view.findViewById(R.id.tv_banktest_case_new);
        mTvBanktestCaseNew.setOnClickListener(this);
        mFlContent = (FrameLayout) view.findViewById(R.id.fl_banktest_content);
        mFlContent.setOnClickListener(this);
    }

    private ArrayList<Fragment> creartFragment() {
        ArrayList<Fragment> list = new ArrayList<>();
        SkillNewFragment skillNewFragment = SkillNewFragment.newInstance("", "");
        ColligateNewFragment colligateNewFragment = ColligateNewFragment.newInstance("", "");
        CaseNewFragment caseNewFragment = CaseNewFragment.newInstance("", "");
        list.add(skillNewFragment);
        list.add(colligateNewFragment);
        list.add(caseNewFragment);
        FragmentManager fm = getFragmentManager();
        for (int i = 0; i < list.size(); i++) {
            fm.beginTransaction().add(FragmentLayout, list.get(i)).hide(list.get(i)).commit();
        }
        fm.beginTransaction().show(skillNewFragment).commit();
        return list;
    }

    /**
     * @param fm     fragment管理器
     * @param list   fragment集合
     * @param layout 显示fragment 布局
     * @param id     要显示的集合的fragment 的几个
     * @return
     */
    private void showSelectFragment(FragmentManager fm, ArrayList<Fragment> list, int layout, int id) {
        if ((id + 1) > list.size()) {
            throw new IndexOutOfBoundsException("超出集合长度");
        }
        if (fm == null) {
            fm = getFragmentManager();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment fragment = list.get(id);
        if (!fragment.isVisible()) {
            if (!fragment.isAdded()) {
                transaction.add(layout, fragment, fragment.getClass().getName());
            } else {
                for (int i = 0; i < list.size(); i++) {
                    fm.beginTransaction().hide(list.get(i)).commit();
                }
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_banktest_skill://技术实务
                changeTextSize(true, false, false);
                showFragment(0);
                break;
            case R.id.tv_banktext_colligate_://综合案例
                changeTextSize(false, true, false);
                showFragment(1);

                break;
            case R.id.tv_banktest_case_new://案例分析
                changeTextSize(false, false, true);
                showFragment(2);
                break;
            default:

        }
    }

    private void changeTextSize(boolean skill, boolean col, boolean cas) {
        mTvBanktestSkill.setTextColor(mContext.getResources().getColor(skill ? R.color.text_title_color : R.color.text_fu_color));
        mTvBanktestSkill.setTextSize(skill ? 24 : 14);
        mTvBanktestSkill.setTypeface(Typeface.DEFAULT, skill ? Typeface.BOLD : Typeface.NORMAL);
        mTvBanktextColligate.setTextColor(mContext.getResources().getColor(col ? R.color.text_title_color : R.color.text_fu_color));
        mTvBanktextColligate.setTextSize(col ? 24 : 14);
        mTvBanktextColligate.setTypeface(Typeface.DEFAULT, col ? Typeface.BOLD : Typeface.NORMAL);
        mTvBanktestCaseNew.setTextColor(mContext.getResources().getColor(cas ? R.color.text_title_color : R.color.text_fu_color));
        mTvBanktestCaseNew.setTextSize(cas ? 24 : 14);
        mTvBanktestCaseNew.setTypeface(Typeface.DEFAULT, cas ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void showFragment(int i) {
        if (fragments != null && !fragments.isEmpty())
            showSelectFragment(getFragmentManager(), fragments, FragmentLayout, i);
    }

    @Override
    public void Start(Progress progress) {
        if (mUpdataUtil != null) {
            mUpdataUtil.setUpdataBankDialogProgress(progress, false, false);
        }

    }

    @Override
    public void OnProgerss(Progress progress) {
        if (mUpdataUtil != null) {
            mUpdataUtil.setUpdataBankDialogProgress(progress, false, false);
        }
        Log.e(TAG, "OnProgerss: ");
    }

    @Override
    public void OnFinish(final File file, Progress progress) {
        if (mUpdataUtil != null) {
            mUpdataUtil.setUpdataBankDialogProgress(progress, false, true);
        }
        jieyaZip(file);
        Log.e(TAG, "OnFinish: ");
    }


    private void jieyaZip(File file) {
        CompressOperate_zip4j zip4j = CompressOperate_zip4j.get_Instance();
        String path = PolyvDevMountInfo.getInstance().getInternalSDCardPath() + File.separator + "xuechuan/databases/";
        zip4j.uncompressZip4j(file.getPath(), path, DataMessageVo.zipkey, new CompressOperate_zip4j.InfaceZip() {
            @Override
            public void UnZipNameSuccess(String path, String name) {
                selectInfom(path);
            }

            @Override
            public void UnZipNameError(String path) {
                Utils.delectFile(new File(path));
            }
        });
    }

    private void selectInfom(String path) {
        SqliteHelp mSqliteHelp = SqliteHelp.get_Instance(mContext);
        SQLiteDatabase mSqLiteDatabase = mSqliteHelp.acquireSqliteDb(path);
        mSqliteHelp.findQuestionSAll(mSqLiteDatabase);
        UserInfomDbHelp mDbHelp = UserInfomDbHelp.get_Instance(mContext);
        mDbHelp.upDateAddQuestion(2);
        if (mUpdataUtil != null) {
            mUpdataUtil.setUpdataBankDialogProgress(null, true, false);
        }
    }

    @Override
    public void OnRemove(Progress progress) {
        Log.e(TAG, "OnRemove: ");
    }

    @Override
    public void OnErrorr(Progress msg) {
        Log.e(TAG, "OnErrorr: ");
    }

    @Override
    public void GradeSuccess(String success) {
        Log.e(TAG, "GradeSuccess: " + success);
        BankGradeVo vo = Utils.getGosnT(success, BankGradeVo.class);
        if (vo.getStatus().getCode() == 200) {
            doGradeBank(vo.getData());
        } else {
            T_ERROR(mContext);
        }
    }

    private void doGradeBank(BankGradeVo.DataBean data) {
        if (data.getType().equals(DataMessageVo.GRADEADD)) {//添加

        } else if (data.getType().equals(DataMessageVo.COVERDREADE)) {//覆盖

        }

    }

    @Override
    public void GradeError(String msg) {
        T_ERROR(mContext);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//显示
            initData();
            UserInfomDbHelp instance = UserInfomDbHelp.get_Instance(mContext);
            UserInfomSqliteVo vo = instance.findUserInfomVo();
            if (vo != null) {
                if (vo.getQuestionversion() == 2) {//已经添加
                } else {
                    initShowUpdataData();
                }
            }
        } else {//隐藏
        }
    }


    @Override
    public void BuySuccess(String content) {
        BuyVo vo = Utils.getGosnT(content, BuyVo.class);
        if (vo.getStatus().getCode() == 200) {
            BuyVo.DataBean data = vo.getData();
            upDataUserInfomBuy(data);

        } else {
            T_ERROR(mContext);
        }
    }

    private void upDataUserInfomBuy(BuyVo.DataBean data) {
        String mSkillBuy = EncryptionUtil.getInstance().getUserBuy(DataMessageVo.BUYSKILL);
        String mColBuy = EncryptionUtil.getInstance().getUserBuy(DataMessageVo.BUYCOL);
        String mCaseBuy = EncryptionUtil.getInstance().getUserBuy(DataMessageVo.BUYCASE);
        String noBuy = EncryptionUtil.getInstance().getUserBuy(DataMessageVo.NOBUY);
        DataMessageVo vo = DataMessageVo.get_Instance();
        vo.setSkillBuy(mSkillBuy);
        vo.setCaseBuy(mCaseBuy);
        vo.setCollorBuy(mColBuy);
        UserInfomDbHelp instance = UserInfomDbHelp.get_Instance(mContext);
        instance.upDateBuy(getString(data.isCourse1(), mSkillBuy, noBuy), getString(data.isCourse2(), mColBuy, noBuy)
                , getString(data.isCourse3(), mCaseBuy, noBuy));
    }

    private String getString(boolean isBuy, String buy, String noBuy) {
        String pasw;
        if (isBuy) {
            pasw = buy;
        } else {
            pasw = noBuy;
        }
        return pasw;
    }

    @Override
    public void BuyError(String msg) {
        T_ERROR(mContext);
    }

    //    每日一练
    @Override
    public void DaySuccess(String success) {
        DayHomeVo homeVo = Utils.getGosnT(success, DayHomeVo.class);
        if (homeVo.getStatus().getCode() == 200) {
            List<DayHomeBeanVo> datas = homeVo.getDatas();
            if (datas == null || datas.isEmpty()) return;
            EventBus.getDefault().postSticky(new DayHomeEvent(1, datas, null));
        } else {
            T_ERROR(mContext);
        }
    }

    @Override
    public void DayError(String msg) {
        T_ERROR(mContext);
    }

    @Override
    public void NoteCountSuccess(String success) {
        NoteListVo listVo = Utils.getGosnT(success, NoteListVo.class);
        if (listVo.getStatus().getCode() == 200) {
            List<NoteListVo.DatasBean> datas = listVo.getDatas();
            if (datas == null || datas.isEmpty()) return;
            EventBus.getDefault().postSticky(new DayHomeEvent(2, null, datas));
        } else {
            T_ERROR(mContext);
        }


    }

    @Override
    public void NoteCountError(String msg) {
        T_ERROR(mContext);
    }
}
