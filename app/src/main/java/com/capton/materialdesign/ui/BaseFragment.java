package com.capton.materialdesign.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by capton on 2017/8/30.
 */
/*
    ButterKnife绑定 依赖库
    compile 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
 */
public abstract class BaseFragment extends Fragment {


    /**
     * 获取Fragment布局Id
     * @return
     */
    public abstract int getLayoutResId();

    /**
     * ButterKnife注解绑定Fragment视图
     * @param LayoutResId
     * @param inflater
     * @param container
     * @return
     */
    public View initView(int LayoutResId,LayoutInflater inflater, @Nullable ViewGroup container){
        View view=inflater.inflate(LayoutResId,container,false);
        ButterKnife.bind(view);
        return view;
    }
}
