package com.fatcat.easy_transfer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference封装
 *
 * @author Fatcat
 */
public class PrefUtils {

    public static final String PREF_NAME = "config";
    public static final String ADD_COURSE = "add_course";

    public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context ctx, String key, boolean value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getAddCourse(Context ctx, String key, String defaultValue) {
        SharedPreferences sp = ctx.getSharedPreferences(ADD_COURSE, Context.MODE_PRIVATE);
        return sp.getString(key,defaultValue);
    }

    public static void setAddCourse(Context ctx, String key, String value){
        SharedPreferences sp = ctx.getSharedPreferences(ADD_COURSE, Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }


}
