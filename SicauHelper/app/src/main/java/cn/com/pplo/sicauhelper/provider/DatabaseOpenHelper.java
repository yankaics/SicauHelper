package cn.com.pplo.sicauhelper.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2014/9/19.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "SICAU_HELPER_DB";
    public static final int DB_VERSION = 1;

    //创建成绩表
    public static final String createScoreSql = "create table " + TableContract.TableScore.TABLE_NAME + "("
            + TableContract.TableScore._ID + " integer primary key autoincrement, "
            + TableContract.TableScore._COURSE + " text, "
            + TableContract.TableScore._MARK + " text, "
            + TableContract.TableScore._CREDIT + " real, "
            + TableContract.TableScore._CATEGORY + " text, "
            + TableContract.TableScore._GRADE + " real"
            + ")";

    //创建课程表
    public static final String createCourseSql = "create table " + TableContract.TableCourse.TABLE_NAME + "("
            + TableContract.TableCourse._ID + " integer primary key autoincrement, "
            + TableContract.TableCourse._NAME + " text, "
            + TableContract.TableCourse._CATEGORY + " text, "
            + TableContract.TableCourse._CREDIT + " real, "
            + TableContract.TableCourse._TIME + " text, "
            + TableContract.TableCourse._CLASSROOM + " text,"
            + TableContract.TableCourse._WEEK + " text,"
            + TableContract.TableCourse._TEACHER + " text,"
            + TableContract.TableCourse._SCHEDULENUM + " integer,"
            + TableContract.TableCourse._SELECTNUM + " integer"
            + ")";

    //创建新闻表
    public static final String createNewsSql = "create table " + TableContract.TableNews.TABLE_NAME + "("
            + TableContract.TableNews._ID + " integer primary key, "
            + TableContract.TableNews._TITLE + " text, "
            + TableContract.TableNews._DATE + " text, "
            + TableContract.TableNews._CATEGORY + " text, "
            + TableContract.TableNews._URL + " text, "
            + TableContract.TableNews._CONTENT + " text, "
            + TableContract.TableNews._SRC + " text "
            + ")";


    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createScoreSql);
        Log.d("winson", "====== 创建数据表2" + createCourseSql);
        db.execSQL(createCourseSql);
        db.execSQL(createNewsSql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}