package com.microsoft.band.sdk.sampleapp.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangyue on 15/10/5.
 */
public class StoreHelper {

    public static final String PREFS_NAME = "AppPrefsFile";

    private static StoreHelper mInstance;

    private Context mCtx;

    private StoreHelper(Context context) {
        this.mCtx = context;
    }

    public static synchronized StoreHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StoreHelper(context);
        }
        return mInstance;
    }

    public String getByKey(String key) {
        SharedPreferences settings = mCtx.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(key, "");
    }

    public Boolean put(String key, String value) {
        SharedPreferences settings = mCtx.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(key, value);

        return editor.commit();
    }
}
