package com.agora.agoracalldemo.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by didiwei on 2022/5/14
 * desc: BaseApplication
 */
public class BaseApplication extends Application {
    private static Application mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Application getContext(){
        return mContext;
    }
}
