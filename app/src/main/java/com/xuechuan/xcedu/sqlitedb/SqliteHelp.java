package com.xuechuan.xcedu.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.db.DbHelp.DatabaseContext;
import com.xuechuan.xcedu.utils.DbQueryUtil;
import com.xuechuan.xcedu.vo.SqliteVo.DeleteSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.QuesitonExamRaltionSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.QuestionChapterSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.QuestionSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.QuestionTagRelationSqliteVo;
import com.xuechuan.xcedu.vo.SqliteVo.TagSqliteVo;

import java.io.File;
import java.util.ArrayList;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.sqlitedb
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018.12.10 上午 10:36
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class SqliteHelp {
    private static volatile SqliteHelp _singleton;
    private Context mContext;
    private final DbQueryUtil mQueryUtil;

    private SqliteHelp(Context context) {
        this.mContext = context;
        mQueryUtil = DbQueryUtil.get_Instance();
    }

    public static SqliteHelp get_Instance(Context context) {
        if (_singleton == null) {
            synchronized (SqliteHelp.class) {
                if (_singleton == null) {
                    _singleton = new SqliteHelp(context);
                }
            }
        }
        return _singleton;
    }

    /**
     * 获取想要打开本地的数据库
     *
     * @param path
     * @param dbNmae
     * @return
     */
    public SQLiteDatabase acquireSqliteDb(String path, String dbNmae) {
        String concat = path.concat(dbNmae);
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(concat), null);
        return database;
    }

    /**
     * 获取想要打开本地的数据库
     *
     * @param path
     * @return
     */
    public SQLiteDatabase acquireSqliteDb(String path) {
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(path), null);
        return database;
    }

    /**
     * 输入要查询的数据库管理器
     *
     * @param sqliteManger
     */
    public Cursor findUserInfomAll(SQLiteDatabase sqliteManger, String dbName) {
        Cursor query = sqliteManger.query(dbName, null, null, null, null, null, null);
        return query;
    }

    public void findQuestionSAll(SQLiteDatabase mSqLiteDatabase) {
        ArrayList<QuestionSqliteVo> question = findQuestion(mSqLiteDatabase);
        ArrayList<DeleteSqliteVo> deleteSqliteVos = finAllQuestioDelete(mSqLiteDatabase);
        ArrayList<QuestionChapterSqliteVo> QuestionChapterSqliteVos = finAllQuestionChapter(mSqLiteDatabase);
        ArrayList<QuestionTagRelationSqliteVo> allQuestionTagRealtion = findAllQuestionTagRealtion(mSqLiteDatabase);
        ArrayList<TagSqliteVo> tagAndTagAll = findTagAndTagAll(mSqLiteDatabase);
        ArrayList<QuesitonExamRaltionSqliteVo> questionExamRealtion = findQuestionExamRealtion(mSqLiteDatabase);
        mSqLiteDatabase.close();
        DatabaseContext context = new DatabaseContext(mContext);
        UserInfomOpenHelp help = new UserInfomOpenHelp(context);
        SQLiteDatabase mDb = help.getWritableDatabase();
        mDb.beginTransaction();
        //添加问题章节
        try {
        if (question != null && !question.isEmpty()) {
            for (int i = 0; i < question.size(); i++) {
                QuestionSqliteVo vo = question.get(i);
                if (queryQuestionIsAdd(mDb, vo.getQuestion_id())) continue;
                ContentValues values = QuestionSqliteHelp.setValues(vo);
                mDb.insert(DataMessageVo.USER_QUESTION_TABLE_QUESTION, null, values);
            }
        }
        if (deleteSqliteVos != null && !deleteSqliteVos.isEmpty()) {
            for (int i = 0; i < deleteSqliteVos.size(); i++) {
                DeleteSqliteVo vo = deleteSqliteVos.get(i);
                if (queryDeleteVo(mDb, vo.getType(), vo.getDelect_id())) continue;
                ContentValues values = DeleteSqliteHelp.setContentValues(vo);
                mDb.insert(DataMessageVo.USER_QUESTION_TABLE_DELETE, null, values);
            }
        }
        if (QuestionChapterSqliteVos != null && !QuestionChapterSqliteVos.isEmpty()) {
            for (int i = 0; i < QuestionChapterSqliteVos.size(); i++) {
                QuestionChapterSqliteVo vo = QuestionChapterSqliteVos.get(i);
                if (queryChapterVo(mDb, vo.getQuestionchapterid())) continue;
                ContentValues values = QuestionChapterSqliteHelp.setValues(vo);
                mDb.insert(DataMessageVo.USER_QEUSTION_TABLE_QUESTION_CHAPTER, null, values);
            }
        }
        if (allQuestionTagRealtion != null && !allQuestionTagRealtion.isEmpty()) {
            for (int i = 0; i < allQuestionTagRealtion.size(); i++) {
                QuestionTagRelationSqliteVo vo = allQuestionTagRealtion.get(i);
                if (quesryIsAdd(mDb, vo.getQuestionid(), vo.getTagid())) continue;
                ContentValues values = QuestionTagreLationSqliteHelp.setValues(vo);
                mDb.insert(DataMessageVo.USER_INFOM_TABLE_QUESTION_TAGRELATION, null, values);

            }
        }
        if (tagAndTagAll != null && !tagAndTagAll.isEmpty()) {
            for (int i = 0; i < tagAndTagAll.size(); i++) {
                TagSqliteVo vo = tagAndTagAll.get(i);
                if (quesrytagIsAdd(mDb, vo.getTagid())) continue;
                ContentValues values = TagSqliteHelp.setContentValues(vo);
                mDb.insert(DataMessageVo.USER_QUESTIONTABLE_TAG, null, values);
            }
        }
        if (questionExamRealtion != null && !questionExamRealtion.isEmpty()) {
            for (int i = 0; i < questionExamRealtion.size(); i++) {
                QuesitonExamRaltionSqliteVo vo = questionExamRealtion.get(i);
                if (quesryExamRaltionVo(mDb, vo.getExamid(), vo.getQuestionid())) continue;
                ContentValues values = new ContentValues();
                values.put("examid", vo.getExamid());
                values.put("questionid", vo.getQuestionid());
                mDb.insert(DataMessageVo.USER_QUESTIONTABLE_EXAMR_ELATION, null, values);
            }
        }
        } catch (Exception e) {
            if (mSqLiteDatabase.isOpen()) {
                mSqLiteDatabase.close();
            }
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

    }

    private boolean quesryExamRaltionVo(SQLiteDatabase mDb, int examid, int questionid) {
        Cursor query = mDb.query(DataMessageVo.USER_QUESTIONTABLE_EXAMR_ELATION, null, "examid=? and questionid=?",
                new String[]{String.valueOf(examid), String.valueOf(questionid)}, null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    /**
     * 查询tag biao
     *
     * @param id
     * @return
     */
    private boolean quesrytagIsAdd(SQLiteDatabase mdb, int id) {

        Cursor query = mdb.query(DataMessageVo.USER_QUESTIONTABLE_TAG, null, "tagid=?",
                new String[]{String.valueOf(id)}, null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    /**
     * 查询是问题否添加
     *
     * @return
     */
    public boolean queryQuestionIsAdd(SQLiteDatabase database, int questionid) {
        Cursor query = database.query(DataMessageVo.USER_QUESTION_TABLE_QUESTION, null, "question_id=?",
                new String[]{String.valueOf(questionid)}, null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;

    }

    /**
     * 查询用户数据
     *
     * @param questionid
     * @param tagid
     * @return
     */
    private boolean quesryIsAdd(SQLiteDatabase database, int questionid, int tagid) {
        Cursor query = database.query(DataMessageVo.USER_INFOM_TABLE_QUESTION_TAGRELATION, null,
                "questionid=? and tagid=?", new String[]{String.valueOf(questionid), String.valueOf(tagid)},
                null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    /**
     * 查询用户删除数据
     *
     * @param type
     * @param delete_id
     * @return
     */
    public boolean queryDeleteVo(SQLiteDatabase mdb, String type, int delete_id) {
        Cursor query = mdb.query(DataMessageVo.USER_QUESTION_TABLE_DELETE, null, "delete_id=? and type=?",
                new String[]{String.valueOf(delete_id), type}, null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    /**
     * 查询是章节否添加
     *
     * @param mDb
     * @return
     */
    public boolean queryChapterVo(SQLiteDatabase mDb, int questionid) {
        Cursor query = mDb.query(DataMessageVo.USER_QEUSTION_TABLE_QUESTION_CHAPTER, null, "questionchapter_id=?",
                new String[]{String.valueOf(questionid)}, null, null, null);
        while (query.moveToNext()) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    private ArrayList<QuesitonExamRaltionSqliteVo> findQuestionExamRealtion(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_questionexamrelation, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        ArrayList<QuesitonExamRaltionSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            QuesitonExamRaltionSqliteVo vo = queryQuesitonExamRaltionData(instance);
            if (vo != null)
                list.add(vo);
        }
        query.close();
        return list;
    }

    private ArrayList<QuestionTagRelationSqliteVo> findAllQuestionTagRealtion(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_questiontagrelation, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        ArrayList<QuestionTagRelationSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            QuestionTagRelationSqliteVo vo = QuestionTagreLationSqliteHelp.getQuestionTagRelationSqliteVo(instance,false);
            if (vo != null)
                list.add(vo);
        }
        query.close();
        return list;
    }

    private ArrayList<TagSqliteVo> findTagAndTagAll(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_tag, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        ArrayList<TagSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            TagSqliteVo vo = TagSqliteHelp.getTagSqliteVo(instance, false);
            if (vo != null)
                list.add(vo);
        }
        query.close();
        return list;
    }

    private ArrayList<QuestionChapterSqliteVo> finAllQuestionChapter(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_questionchapter, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        ArrayList<QuestionChapterSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            QuestionChapterSqliteVo sqliteVo = QuestionChapterSqliteHelp.getQuestionChapterSqliteVo(instance, false);
            if (sqliteVo != null) {
                list.add(sqliteVo);
            }
        }
        query.close();
        return list;
    }

    private ArrayList<DeleteSqliteVo> finAllQuestioDelete(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_delete, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        ArrayList<DeleteSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            DeleteSqliteVo vo = DeleteSqliteHelp.getDeleteValue(instance, false);
            if (vo != null) {
                list.add(vo);
            }
        }
        query.close();
        return list;
    }

    private ArrayList<QuestionSqliteVo> findQuestion(SQLiteDatabase mSqLiteDatabase) {
        Cursor query = mSqLiteDatabase.query(DataMessageVo.t_question, null, null, null, null, null, null);
        DbQueryUtil instance = DbQueryUtil.get_Instance();
        instance.initCursor(query);
        final ArrayList<QuestionSqliteVo> list = new ArrayList<>();
        while (query.moveToNext()) {
            final QuestionSqliteVo sqliteVo = QuestionSqliteHelp.getQuesitonSqlite(instance, false);
            if (sqliteVo != null) {
                list.add(sqliteVo);
            }
        }
        query.close();
        return list;
    }

    private QuesitonExamRaltionSqliteVo queryQuesitonExamRaltionData(DbQueryUtil mQueryUtil) {
        QuesitonExamRaltionSqliteVo values = new QuesitonExamRaltionSqliteVo();
        int examid = mQueryUtil.queryInt("examid");
        int questionid = mQueryUtil.queryInt("questionid");
        values.setExamid(examid);
        values.setQuestionid(questionid);
        return values;
    }
}
