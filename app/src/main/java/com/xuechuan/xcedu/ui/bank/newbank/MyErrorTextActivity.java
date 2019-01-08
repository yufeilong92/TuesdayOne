package com.xuechuan.xcedu.ui.bank.newbank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.adapter.bank.GmErrorTextAdapter;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.mvp.presenter.ErrorTextPresenter;
import com.xuechuan.xcedu.service.SubmiteProgressQuestionService;
import com.xuechuan.xcedu.sqlitedb.DoLogProgressSqliteHelp;
import com.xuechuan.xcedu.sqlitedb.DoUpLogProgressSqliteHelp;
import com.xuechuan.xcedu.sqlitedb.ErrorSqlteHelp;
import com.xuechuan.xcedu.sqlitedb.QuestionChapterSqliteHelp;
import com.xuechuan.xcedu.sqlitedb.QuestionSqliteHelp;
import com.xuechuan.xcedu.ui.bank.newBankActivity.ErrorTextActivity;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.vo.ErrorCollectVo;
import com.xuechuan.xcedu.vo.SqliteVo.DoLogProgreeSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.ErrorSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.QuestionChapterSqliteVo;
import com.xuechuan.xcedu.vo.UpQuestionProgressVo;

import java.util.ArrayList;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: MyErrorTextActivity
 * @Package com.xuechuan.xcedu.ui.bank
 * @Description: 我的错题/收藏(新的)
 * @author: L-BackPacker
 * @date: 2018/5/3 20:09
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/3
 */
public class MyErrorTextActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 错题或收藏
     */
    private static final String ERRORCOLNUMBER = "errorcol";
    /**
     * 问题di
     */
    private static final String MQUESTION = "question";
    private ImageView mIvBMore;
    private TextView mTvErrorNumber;
    private Button mBtnGoDoText;
    private RecyclerView mRlvErrorList;
    private ErrorTextPresenter mPresenter;
    private Context mContext;
    /**
     * 科目id
     */
    private static String COURESID = "couresid";
    /**
     * 题干类型
     */
    private static String TEXTTYPE = "texttype";
    public static String ERRTYPE = "err";
    public static String FAVTYPE = "fav";
    private String mCouresid;
    private AlertDialog mDialog;
    private String mType;
    private TextView mTvErrorText;
    private LinearLayout mLlErrorHear;
    //类型内容
    private String content;
    private String mNumber;
    private String mQuestion;
    private ErrorSqlteHelp mErrorSqlteHelp;
    private QuestionSqliteHelp mSqliteHelp;
    private Object allData;
    private ArrayList<ErrorCollectVo> mDataLists;
    private QuestionChapterSqliteHelp mChapterSqliteHelp;
    private DoLogProgressSqliteHelp mProgressSqliteHelp;
    private ArrayList<ErrorCollectVo> mAlllists;
    private DoUpLogProgressSqliteHelp mDoUpLogProgressSqliteHelp;

    /**
     * @param context
     * @param Couresid 科目
     * @param number   数量
     * @return
     */
    public static Intent newInstance(Context context, String Couresid, String number) {
        Intent intent = new Intent(context, MyErrorTextActivity.class);
        intent.putExtra(COURESID, Couresid);
        intent.putExtra(ERRORCOLNUMBER, number);
        return intent;
    }

    /*   @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_error_text);
           initView();
       }
   */
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_error_text);
        if (getIntent() != null) {
            mCouresid = getIntent().getStringExtra(COURESID);
            mNumber = getIntent().getStringExtra(ERRORCOLNUMBER);
        }
        initView();
        initUtils();
        initData(mNumber);
        mDataLists = getAllData();
        initAdapter();
        submitProgress();
        submitProgressOne();
    }



    private void initAdapter() {
        setGridLayoutManger(mContext, mRlvErrorList, 1);
        GmErrorTextAdapter adapter = new GmErrorTextAdapter(mContext, mDataLists);
        mRlvErrorList.setAdapter(adapter);
        adapter.setClickListener(new GmErrorTextAdapter.onItemClickListener() {
            @Override
            public void onClickListener(ErrorCollectVo vo, int position) {
                Intent intent = ErrorTextActivity.start_Intent(mContext, mCouresid, vo.getChild(), 1, vo.getChapter_id());
                startActivity(intent);
            }
        });
    }

    private void initUtils() {
        //错题表
        mErrorSqlteHelp = ErrorSqlteHelp.getInstance(mContext);
        //本地题库
        mSqliteHelp = QuestionSqliteHelp.get_Instance(mContext);
        //章节数据库
        mChapterSqliteHelp = QuestionChapterSqliteHelp.get_Instance(mContext);
        //进度表
        mProgressSqliteHelp = DoLogProgressSqliteHelp.get_Instance(mContext);
        //上传进度
        mDoUpLogProgressSqliteHelp = DoUpLogProgressSqliteHelp.get_Instance(mContext);

    }

    private void initData(String mNumber) {
        content = getString(R.string.myError);
        mLlErrorHear.setBackgroundResource(R.mipmap.ic_wt_bg);
        mTvErrorText.setText(content);
        if (mNumber.equals("0")) {
            mBtnGoDoText.setClickable(false);
            mBtnGoDoText.setBackgroundResource(R.drawable.btn_errortext_no_bg);
        } else {
            mBtnGoDoText.setClickable(true);
            mBtnGoDoText.setBackgroundResource(R.drawable.btn_errortext_go_bg);
        }
        mTvErrorNumber.setText(mNumber);

    }

    public ArrayList<ErrorCollectVo> getAllData() {
        ArrayList<ErrorSqliteVo> AlQuestionlists = mErrorSqlteHelp.getErrorLists(Integer.parseInt(mCouresid));
        if (mAlllists != null) {
            mAlllists.clear();
        }
        mAlllists = new ArrayList<>();
        for (int i = 0; i < AlQuestionlists.size(); i++) {
            ErrorSqliteVo sqliteVo = AlQuestionlists.get(i);
            ErrorCollectVo vo = new ErrorCollectVo();
            vo.setCoursed(Integer.parseInt(mCouresid));
            vo.setQuestionid(sqliteVo.getQuesitonid());
            mAlllists.add(vo);
        }
        ArrayList<Integer> mIntegerLists = new ArrayList<>();
        if (AlQuestionlists != null && !AlQuestionlists.isEmpty()) {
            for (int i = 0; i < AlQuestionlists.size(); i++) {
                ErrorSqliteVo vo = AlQuestionlists.get(i);
                if (!mIntegerLists.contains(vo.getChapterid())) {
                    mIntegerLists.add(vo.getChapterid());
                }
            }
        }
        if (mIntegerLists.isEmpty()) return null;
        ArrayList<ErrorCollectVo> Chapterlist = new ArrayList<>();
        for (int i = 0; i < mIntegerLists.size(); i++) {
            Integer integer = mIntegerLists.get(i);
            ErrorCollectVo vo = new ErrorCollectVo();
            vo.setChapter_id(integer);
            QuestionChapterSqliteVo charpter = mChapterSqliteHelp.queryCharpterWithChapterid(integer);
            if (charpter != null) {
                vo.setChapter_name(charpter.getChaptername());
                vo.setCoursed(charpter.getCourseid());
                DoLogProgreeSqliteVo progress = mProgressSqliteHelp.findLookWithTidChapterId(integer, charpter.getCourseid(), DataMessageVo.ERROR_FOUR);
                if (progress == null) {
                    vo.setNum(0);
                } else {
                    vo.setNum(Integer.parseInt(progress.getNumber()));
                }
                Chapterlist.add(vo);
            } else {
                continue;
            }
        }
        //拿到章节id
        if (Chapterlist != null && !Chapterlist.isEmpty()) {
            for (int i = 0; i < AlQuestionlists.size(); i++) {
                //所有题
                ErrorSqliteVo sqliteVo = AlQuestionlists.get(i);
                for (int k = 0; k < Chapterlist.size(); k++) {
                    //所有章节
                    ErrorCollectVo vo = Chapterlist.get(k);
                    if (vo.getChild() == null || vo.getChild().isEmpty()) {//没有内容
                        ArrayList<ErrorCollectVo> listChild = new ArrayList<>();
                        if (vo.getChapter_id() == sqliteVo.getChapterid()) {
                            ErrorCollectVo collectVo = new ErrorCollectVo();
                            collectVo.setCoursed(vo.getCoursed());
                            collectVo.setId(vo.getId());
                            collectVo.setChapter_id(sqliteVo.getChapterid());
                            collectVo.setQuestionid(sqliteVo.getQuesitonid());
                            listChild.add(collectVo);
                        }
                        vo.setChild(listChild);
                    } else {//有内容往里加入
                        ArrayList<ErrorCollectVo> child = vo.getChild();
                        if (vo.getChapter_id() == sqliteVo.getChapterid()) {
                            ErrorCollectVo collectVo = new ErrorCollectVo();
                            collectVo.setCoursed(vo.getCoursed());
                            collectVo.setId(vo.getId());
                            collectVo.setChapter_id(sqliteVo.getChapterid());
                            collectVo.setQuestionid(sqliteVo.getQuesitonid());
                            child.add(collectVo);
                        }
                        vo.setChild(child);
                    }

                }
            }
        } else {
            return null;
        }
        return Chapterlist;
    }

    private void initView() {
        mIvBMore = (ImageView) findViewById(R.id.iv_b_more);
        mTvErrorNumber = (TextView) findViewById(R.id.tv_error_number);
        mBtnGoDoText = (Button) findViewById(R.id.btn_go_do_text);
        mRlvErrorList = (RecyclerView) findViewById(R.id.rlv_error_list);
        mContext = this;
        mBtnGoDoText.setOnClickListener(this);
        mTvErrorText = (TextView) findViewById(R.id.tv_error_text);
        mTvErrorText.setOnClickListener(this);
        mLlErrorHear = (LinearLayout) findViewById(R.id.ll_error_hear);
        mLlErrorHear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_do_text:
                Intent intent = ErrorTextActivity.start_Intent(mContext, mCouresid, mAlllists, 1,
                        DataMessageVo.error_mark);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mDataLists != null) {
            mDataLists.clear();
        }
        mDataLists = getAllData();
        if (mAlllists == null || mAlllists.isEmpty()) {
            initData(String.valueOf(0));
        } else
            initData(String.valueOf(mAlllists.size()));
        initAdapter();
        submitProgress();
        submitProgressOne();
    }

    private void submitProgress() {
        ArrayList<DoLogProgreeSqliteVo> datas = mDoUpLogProgressSqliteHelp.queryAllProgress(DataMessageVo.ERROR_FOUR, Integer.parseInt(mCouresid));
        if (datas == null || datas.isEmpty()) return;
        ArrayList<UpQuestionProgressVo> list = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            DoLogProgreeSqliteVo vo = datas.get(i);
            UpQuestionProgressVo progressVo = new UpQuestionProgressVo();
            progressVo.setId(vo.getId());
            progressVo.setTagetid(vo.getChapterid());
            progressVo.setRnum(StringUtil.isEmpty(vo.getNumber()) ? 0 : Integer.parseInt(vo.getNumber()));
            progressVo.setQt(5);
            list.add(progressVo);
        }
        SubmiteProgressQuestionService.startActionFoo(mContext, list, Integer.parseInt(mCouresid));
    }
    private void submitProgressOne() {
        ArrayList<DoLogProgreeSqliteVo> datas = mDoUpLogProgressSqliteHelp.queryAllProgress(DataMessageVo.ERROR_SIX, Integer.parseInt(mCouresid));
        if (datas == null || datas.isEmpty()) return;
        ArrayList<UpQuestionProgressVo> list = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            DoLogProgreeSqliteVo vo = datas.get(i);
            UpQuestionProgressVo progressVo = new UpQuestionProgressVo();
            progressVo.setId(vo.getId());
            progressVo.setTagetid(0);
            progressVo.setRnum(StringUtil.isEmpty(vo.getNumber()) ? 0 : Integer.parseInt(vo.getNumber()));
            progressVo.setQt(4);
            list.add(progressVo);
        }
        SubmiteProgressQuestionService.startActionFoo(mContext, list, Integer.parseInt(mCouresid));
    }
}
