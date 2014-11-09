package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by winson on 2014/11/6.
 */
public class SharedPreferencesUtil {
    public static final String NAME = "SICAU_HELPER_SHARED";

    /**
     * 登录学号
     */
    public static final String LOGIN_SID = "LOGIN_SID";
    /**
     * 登录密码
     */
    public static final String LOGIN_PSWD = "LOGIN_PSWD";

    /**
     * 以xml保存键值对
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(value instanceof String) {
            editor.putString(key, (String) value);

        }
        editor.apply();
        editor.commit();
    }

    public static Object get(Context context, String key, Object defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if(defaultValue instanceof String) {
            return sp.getString(key, (String) defaultValue);
        }
        else {
            return null;
        }
    }

}