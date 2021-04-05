package com.pleiades.pleione.kakaoprofile.prefs;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pleiades.pleione.kakaoprofile.ui.activity.main.MainActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_LENGTH_LOWER_BOUND;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_NEWEST_FIRST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_SPAN_COUNT;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.DEFAULT_VERSION;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_HIDE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_LAST_VERSION_CODE;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_LENGTH_LOWER_BOUND;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_NOT_NEW_PROFILE_NAME_LIST;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_PERMISSION_URI;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_REMOVE_ADS;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_ARCHIVE;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SORT_PROFILES;
import static com.pleiades.pleione.kakaoprofile.prefs.PrefsConfig.KEY_SPAN_COUNT;

public class PrefsController {
    // get int
    public static int getInt(String key) {
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        int result;

        switch (key) {
            case KEY_SPAN_COUNT:
                result = prefs.getInt(KEY_SPAN_COUNT, DEFAULT_SPAN_COUNT);
                break;
            case KEY_SORT_PROFILES:
                result = prefs.getInt(KEY_SORT_PROFILES, DEFAULT_NEWEST_FIRST);
                break;
            case KEY_SORT_ARCHIVE:
                result = prefs.getInt(KEY_SORT_ARCHIVE, DEFAULT_NEWEST_FIRST);
                break;
            case KEY_LENGTH_LOWER_BOUND:
                result = prefs.getInt(KEY_LENGTH_LOWER_BOUND, DEFAULT_LENGTH_LOWER_BOUND);
                break;
            case KEY_LAST_VERSION_CODE:
                result = prefs.getInt(KEY_LAST_VERSION_CODE, DEFAULT_VERSION);
                break;
            default:
                result = 0;
        }

        return result;
    }

    // get boolean
    public static boolean getBoolean(String key){
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);

        boolean result = false;

        switch(key){
            case KEY_REMOVE_ADS:
                result = prefs.getBoolean(KEY_REMOVE_ADS, false);
        }
        return result;
    }

    // get string
    public static String getString(String key) {
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);

        String result = null;

        switch(key){
            case KEY_PERMISSION_URI:
                result = prefs.getString(KEY_PERMISSION_URI, null);
        }
        return result;
    }

    // set boolean
    public static void setBoolean(String key, boolean value){
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // set string
    public static void setString(String key, String value){
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // set int
    public static void setInt(String key, int value) {
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // set array list
    public static void setListPrefs(String key, ArrayList value) {
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, new Gson().toJson(value));
        editor.apply();
    }

    // get array list
    public static ArrayList getListPrefs(String key) {
        final SharedPreferences prefs = MainActivity.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(key, null);

        if (json != null) {
            switch (key) {
                // string array list
                case KEY_NOT_NEW_PROFILE_NAME_LIST:
                case KEY_HIDE_NAME_LIST:
                    return gson.fromJson(json, new TypeToken<ArrayList<String>>() {
                    }.getType());
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

}
