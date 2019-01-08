package com.xuechuan.xcedu.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.db.DbHelp.DatabaseContext;
import com.xuechuan.xcedu.utils.DbQueryUtil;
import com.xuechuan.xcedu.vo.SqliteVo.DologSqliteVo;

import java.util.ArrayList;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: ErrorSqlteHelp.java
 * @Package com.xuechuan.xcedu.sqlitedb
 * @Description: 用户做题记录表（用于记录正确数）
 * @author: YFL
 * @date: 2018/12/25 22:36
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/12/25 星期二
 * 注意：本内容仅限于学川教育有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class DoLogSqlteHelp {
    private Context mContext;
    private static volatile DoLogSqlteHelp _instance = null;
    private final DbQueryUtil mDbQueryUtil;
    private final SQLiteDatabase mSqLiteDatabase;

    private DoLogSqlteHelp(Context context) {
        this.mContext = context;
        mSqLiteDatabase = createtable();
        mDbQueryUtil = DbQueryUtil.get_Instance();
    }

    public static DoLogSqlteHelp getInstance(Context context) {
        if (_instance == null) {
            synchronized (DoLogSqlteHelp.class) {
                if (_instance == null) {
                    _instance = new DoLogSqlteHelp(context);
                }
            }
        }
        return _instance;
    }

    private SQLiteDatabase createtable() {
        DatabaseContext context = new DatabaseContext(mContext);
        UserInfomOpenHelp userInfomOpenHelp = UserInfomOpenHelp.get_Instance(context);
        return userInfomOpenHelp.getWritableDatabase();
    }

    private boolean empty() {
        if (mSqLiteDatabase == null)
            return true;
        if (mSqLiteDatabase.isReadOnly())
            return true;
        return false;
    }

    public void addDoLogItem(DologSqliteVo sqliteVo) {
        if (empty()) return;
        DologSqliteVo vo = queryIsAdd(sqliteVo.getCourseid(), sqliteVo.getChapterid(), sqliteVo.getQuestion_id());
        if (vo == null) {
            ContentValues values = getContentValues(sqliteVo);
            mSqLiteDatabase.insert(DataMessageVo.USER_QUESTION_DOTEXT_TABLE, null, values);
        } else {
            ContentValues values = getContentValues(sqliteVo);
            mSqLiteDatabase.update(DataMessageVo.USER_QUESTION_DOTEXT_TABLE, values, "id=?",
                    new String[]{String.valueOf(vo.getId())});
        }

    }

    //根据科目获取用户做题数据
    public ArrayList<DologSqliteVo> queryList(int courseid) {
        if (empty()) return null;
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTION_DOTEXT_TABLE, null,
                "courseid=?", new String[]{String.valueOf(courseid)}, null, null,
                null);
        mDbQueryUtil.initCursor(query);
        ArrayList<DologSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            DologSqliteVo vo = getDoLogSqliteVo(mDbQueryUtil);
            list.add(vo);
        }
        return list;
    }

    public DologSqliteVo queryIsAdd(int courseid, int chapterid, int questionid) {
        if (empty()) return null;
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTION_DOTEXT_TABLE, null,
                "courseid=? and chapterid=? and question_id=?",
                new String[]{String.valueOf(courseid), String.valueOf(chapterid), String.valueOf(questionid)},
                null, null, null);
        mDbQueryUtil.initCursor(query);
        while (query.moveToNext()) {
            DologSqliteVo vo = getDoLogSqliteVo(mDbQueryUtil);
            return vo;
        }
        return null;
    }

    private ContentValues getContentValues(DologSqliteVo sqliteVo) {
        ContentValues values = new ContentValues();
        values.put("question_id", sqliteVo.getQuestion_id());
        values.put("questiontype", sqliteVo.getQuestiontype());
        values.put("chapterid", sqliteVo.getChapterid());
        values.put("time", sqliteVo.getTime());
        values.put("courseid", sqliteVo.getCourseid());
        return values;
    }

    private DologSqliteVo getDoLogSqliteVo(DbQueryUtil mDbQueryUtil) {
        DologSqliteVo vo = new DologSqliteVo();
        int id = mDbQueryUtil.queryInt("id");
        int question_id = mDbQueryUtil.queryInt("question_id");
        int questiontype = mDbQueryUtil.queryInt("questiontype");
        int chapterid = mDbQueryUtil.queryInt("chapterid");
        int courseid = mDbQueryUtil.queryInt("courseid");
        String time = mDbQueryUtil.queryString("time");
        vo.setTime(time);
        vo.setId(id);
        vo.setChapterid(chapterid);
        vo.setCourseid(courseid);
        vo.setQuestion_id(question_id);
        vo.setQuestiontype(questiontype);
        return vo;

    }

}
