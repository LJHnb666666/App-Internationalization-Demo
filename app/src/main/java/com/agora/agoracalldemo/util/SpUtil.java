package com.agora.agoracalldemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.agora.agoracalldemo.base.BaseApplication;

/**
 * Created by didiwei on 2022/5/12
 * desc: SharedPreference工具类
 *
 * 因为目前demo还没有设置语言的功能，所以与SP加载本地保存语言的相关操作其实目前都用不上
 */
public class SpUtil {
    private static final String APP_SP = "app_sp";
    private static final String TAG = SpUtil.class.getSimpleName();

    private SpUtil() {    }

    private static SpUtil instance = new SpUtil();
    private static SharedPreferences mSp = null;
    public static SpUtil getInstance() {
        if (mSp == null) {
            synchronized (SpUtil.class){
                if(mSp == null){
                    mSp = BaseApplication.getContext().getSharedPreferences(APP_SP, Context.MODE_PRIVATE);
                }
            }
        }
        return instance;
    }

    //保存数据
    public void save(String key, Object value) {
        if (value == null) {
            Log.v(TAG, "value==null保存失败");
            return;
        }
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    public void clearAll() {
        mSp.edit().clear().commit();
    }
}

