package com.capton.materialdesign.application;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.capton.materialdesign.R;
import com.scwang.smartrefresh.header.WaveSwipeHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.tencent.bugly.Bugly;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import me.wcy.music.BuildConfig;
import me.wcy.music.http.HttpInterceptor;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.Preferences;
import okhttp3.OkHttpClient;

public class App extends android.app.Application {
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new WaveSwipeHeader(context);//默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //默认是 BallPulseFooter
                return new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Translate);

            }
        });

    }
    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.getSplashDir(this);
        Utils.init(this);
        AppCache.init(this);
        AppCache.updateNightMode(Preferences.isNightMode());
        initOkHttpUtils();
        initBugly();
    }

    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initBugly() {
        if (!BuildConfig.DEBUG) {
            Bugly.init(this, BuildConfig.BUGLY_APP_ID, false);
        }
    }
}
