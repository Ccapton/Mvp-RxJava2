package com.capton.materialdesign.ui;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.capton.materialdesign.view.IView;

import butterknife.ButterKnife;


/**
 * Created by capton on 2017/8/30.
 */
/*
    ButterKnife绑定 依赖库
    compile 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //  setSystemBarTransparent();
        initView(setLayoutResId());
        setListenr();
    }

    public abstract void setListenr();

    /**
     * 设置Activity布局Id
     * @return
     */
    public abstract int setLayoutResId();

    /**
     * ButterKnife注解绑定主视图
     * @param layoutResId
     */
    public void initView(int layoutResId){
        setContentView(layoutResId);
        ButterKnife.bind(this);
    }

    public void setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
