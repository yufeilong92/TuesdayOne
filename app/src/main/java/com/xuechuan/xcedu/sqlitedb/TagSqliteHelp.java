package com.xuechuan.xcedu.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.db.DbHelp.DatabaseContext;
import com.xuechuan.xcedu.utils.DbQueryUtil;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.vo.SqliteVo.TagSqliteVo;

import java.io.File;
import java.util.ArrayList;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.sqlitedb
 * @Description:tag tag帮助类
 * @author: L-BackPacker
 * @date: 2018.12.11 下午 3:04
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class TagSqliteHelp {
    private static volatile TagSqliteHelp _singleton;
    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private final DbQueryUtil mDbQueryUtil;

    private TagSqliteHelp(Context context) {
        this.mContext = context;
        mSqLiteDatabase = createtable();
        mDbQueryUtil = DbQueryUtil.get_Instance();
    }

    public static TagSqliteHelp get_Instance(Context context) {
        if (_singleton == null) {
            synchronized (TagSqliteHelp.class) {
                if (_singleton == null) {
                    _singleton = new TagSqliteHelp(context);
                }
            }
        }
        return _singleton;
    }

    private SQLiteDatabase createtable() {
        DatabaseContext context = new DatabaseContext(mContext);
//        UserInfomOpenHelp userInfomOpenHelp =new UserInfomOpenHelp(context);
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

    public void close() {
        if (empty()) return;
        mSqLiteDatabase.close();
    }

    public void addTagItem(TagSqliteVo sqliteVo) {
        if (empty()) return;
        try {
            if (quesryIsAdd(sqliteVo.getTagid())) return;
            ContentValues values = setContentValues(sqliteVo);
            mSqLiteDatabase.insert(DataMessageVo.USER_QUESTIONTABLE_TAG, null, values);
        } catch (Exception e) {
            if (mSqLiteDatabase.isOpen()) {
                mSqLiteDatabase.close();
            }
            e.printStackTrace();
        }

    }

    /**
     * 更新用户观看记录数据
     *
     * @param tagid
     * @param looknum
     */
    public void upDataUseLook(int tagid, int looknum) {
        if (empty()) return;
        if (!quesryIsAdd(tagid)) return;
        ContentValues values = new ContentValues();
        values.put("looknum", looknum);
        mSqLiteDatabase.update(DataMessageVo.USER_QUESTIONTABLE_TAG, values, "tagid=?",
                new String[]{String.valueOf(tagid)});

    }

    /**
     * 批量添加
     *
     * @param list
     */
    public void addListTagItem(ArrayList<TagSqliteVo> list) {
        if (empty()) return;
        try {
//            mSqLiteDatabase.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                TagSqliteVo tagSqliteVo = list.get(i);
                if (quesryIsAdd(tagSqliteVo.getTagid())) continue;
                ContentValues values = setContentValues(tagSqliteVo);
                mSqLiteDatabase.insert(DataMessageVo.USER_QUESTIONTABLE_TAG, null, values);
            }
//            mSqLiteDatabase.setTransactionSuccessful();
//            mSqLiteDatabase.endTransaction();
            mSqLiteDatabase.close();

        } catch (Exception e) {
            if (mSqLiteDatabase.isOpen()) {
                mSqLiteDatabase.close();
            }
            e.printStackTrace();
        }

    }

    private boolean quesryIsAdd(int id) {
        if (empty()) return false;
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTIONTABLE_TAG, null, "tagid=?",
                new String[]{String.valueOf(id)}, null, null, null);
        while (query.moveToNext()) {
            return true;
        }
        return false;
    }

    public static ContentValues setContentValues(TagSqliteVo sqliteVo) {
        ContentValues values = new ContentValues();
        values.put("tagname", sqliteVo.getTagname());
        values.put("courseid", sqliteVo.getCourseid());
        values.put("questionnum", sqliteVo.getQuestionnum());
        values.put("tagid", sqliteVo.getTagid());
        values.put("looknum", sqliteVo.getLooknum());
        return values;
    }


    public ArrayList<TagSqliteVo> findTagAll() {
        if (empty()) return null;
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTIONTABLE_TAG, null, null, null, null
                , null, null);
        mDbQueryUtil.initCursor(query);
        ArrayList<TagSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            TagSqliteVo vo = getTagSqliteVo(mDbQueryUtil, true);
            list.add(vo);
        }
        return list;

    }

    /**
     * 更具科目id 查询数据对应专项练习
     *
     * @param courseid
     * @return
     */
    public ArrayList<TagSqliteVo> queryTagAllWithCourserid(String courseid) {
        if (empty()) {
            return null;
        }
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTIONTABLE_TAG, null,
                "courseid=?", new String[]{courseid}, null, null, null);
        mDbQueryUtil.initCursor(query);
        ArrayList<TagSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            TagSqliteVo tagSqliteVo = getTagSqliteVo(mDbQueryUtil, true);
            list.add(tagSqliteVo);
        }
        return list;

    }

    /**
     * 更具tagid 查询数据
     * @param tagid
     * @return
     */
    public TagSqliteVo queryTagVoWitgId(String tagid) {
        if (empty()) return null;
        Cursor query = mSqLiteDatabase.query(DataMessageVo.USER_QUESTIONTABLE_TAG, null, "tagid=?"
                , new String[]{tagid}, null, null, null);
        mDbQueryUtil.initCursor(query);
        while (query.moveToNext()) {
            TagSqliteVo vo = getTagSqliteVo(mDbQueryUtil, true);
            return vo;
        }
        return null;
    }


    public synchronized static TagSqliteVo getTagSqliteVo(DbQueryUtil mDbQueryUtil, boolean load) {
        TagSqliteVo vo = new TagSqliteVo();
        int id = mDbQueryUtil.queryInt("id");
        if (load) {
            int tagid = mDbQueryUtil.queryInt("tagid");
            vo.setTagid(tagid);
            vo.setId(id);
            int looknum = mDbQueryUtil.queryInt("looknum");
            vo.setLooknum(looknum);
        } else {
            vo.setTagid(id);
        }
        String tagname = mDbQueryUtil.queryString("tagname");
        int courseid = mDbQueryUtil.queryInt("courseid");
        int questionnum = mDbQueryUtil.queryInt("questionnum");
        vo.setCourseid(courseid);
        vo.setQuestionnum(questionnum);
        vo.setTagname(tagname);
        return vo;
    }
}
