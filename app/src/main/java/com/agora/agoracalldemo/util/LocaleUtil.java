package com.agora.agoracalldemo.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.agora.agoracalldemo.MainActivity;
import com.hyphenate.easeim.R;

import java.util.Locale;

/**
 * Created by didiwei on 2022/5/12
 * desc: 用于设置保存语言以及获取当前语言，重启App等操作
 *
 * 因为目前demo还没有设置语言的功能，所以与SP加载本地保存语言的相关操作其实目前都用不上，只用到了 加载系统默认的语言类型 这个功能。
 * 也就是说，目前demo的语言只和手机系统语言相一致
 */
public class LocaleUtil {
    /**
     * 获取用户设置的Locale
      * @return
     */
    public static Locale getUserLocale() {
        int currentLanguage = SpUtil.getInstance().getInt("currentLanguage", 0);
        Locale myLocale = Locale.SIMPLIFIED_CHINESE;//默认是中问
        switch (currentLanguage) {
            case 0:
                myLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                myLocale = Locale.ENGLISH;
                break;
            default:
                myLocale = Locale.SIMPLIFIED_CHINESE;
                break;
        }
        return myLocale;
    }

    /**
     * 设置语言：如果之前有设置就遵循设置如果没设置过就跟随系统语言
     * @param context
     */
    public static void changeAppLanguage(Context context) {
        if (context == null) return;

        Context appContext = context.getApplicationContext();
        int currentLanguage = SpUtil.getInstance().getInt("currentLanguage", -1);
        Locale myLocale;        // 0 简体中文 1 English
        switch (currentLanguage) {
             case 0:
                 myLocale = Locale.SIMPLIFIED_CHINESE;
                 break;
             case 1:
                 myLocale = Locale.ENGLISH;
                 break;
             default:
                 myLocale = appContext.getResources().getConfiguration().locale;
                 break;
        }

        // 本地语言设置
        if (needUpdateLocale(appContext, myLocale)) {
             updateLocale(appContext, myLocale);
        }
    }

    /**
     * 保存设置的语言
     * @param context
     * @param currentLanguage
     */
    public static void changeAppLanguage(Context context, int currentLanguage) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        SpUtil.getInstance().save("currentLanguage", currentLanguage);
        Locale myLocale = Locale.SIMPLIFIED_CHINESE;        // 0 简体中文 1 English
         switch (currentLanguage) {
             case 0:
                 myLocale = Locale.SIMPLIFIED_CHINESE;
                 break;
             case 1:
                 myLocale = Locale.ENGLISH;
                 break;
             default:
                 myLocale = Locale.SIMPLIFIED_CHINESE;
                 break;
         }
         // 本地语言设置
         if (LocaleUtil.needUpdateLocale(appContext, myLocale)) {
             LocaleUtil.updateLocale(appContext, myLocale);
         }
         Toast.makeText(appContext, appContext.getString(R.string.LANGUAGE_SET_SUCCESS), Toast.LENGTH_SHORT).show();
         restartApp(appContext);
    }

    /**
     * 重启app生效
     * @param context
     */
    public static void restartApp(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取当前的Locale
      * @param context
     * @return
     */
    public static Locale getCurrentLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0有多语言设置获取顶部的语言
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * 更新Locale
     * @param context
     * @param locale
     */
    public static void updateLocale(Context context, Locale locale) {
        if (needUpdateLocale(context, locale)) {
            Configuration configuration = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= 19) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, displayMetrics);
        }
    }

    /**
     * 判断需不需要更新
     * @param context
     * @param locale
     * @return
     */
    public static boolean needUpdateLocale(Context context, Locale locale) {
        return locale != null && !getCurrentLocale(context).equals(locale);
    }

    /**
     * 当系统语言发生改变的时候还是继续遵循用户设置的语言
     * @param context
     * @param newConfig
     */
    public static void setLanguage(Context context, Configuration newConfig) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        int currentLanguage = SpUtil.getInstance().getInt("currentLanguage", -1);
        Locale locale;        // 0 简体中文 1 English
         switch (currentLanguage) {
             case 0:
                 locale = Locale.SIMPLIFIED_CHINESE;
                 break;
             case 1:
                 locale = Locale.ENGLISH;
                 break;
             default:
                 locale = appContext.getResources().getConfiguration().locale;
                 break;
         }

         // 系统语言改变了应用保持之前设置的语言
         if (locale != null) {
             Locale.setDefault(locale);
             Configuration configuration = new Configuration(newConfig);
             if (Build.VERSION.SDK_INT >= 19) {
                 configuration.setLocale(locale);
             } else {
                 configuration.locale = locale;
             }
             appContext.getResources().updateConfiguration(configuration, appContext.getResources().getDisplayMetrics());
         }
    }
}
