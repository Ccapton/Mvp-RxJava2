package com.capton.materialdesign.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.capton.materialdesign.R;
import com.capton.materialdesign.ui.BaseActivity;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;
import cn.hugeterry.coordinatortablayout.listener.LoadHeaderImagesListener;

/**
 * Created by capton on 2017/8/31.
 */

public class NewsActivity extends BaseActivity   {

    private String channels[]=new String []{ "头条", "新闻", "军事","科技"};
        //    "教育",  "NBA", "股票", "星座", "女性", "健康", "育儿","财经", "体育", "NBA"

    @BindView(R.id.channelVp)
    ViewPager viewPager;
    @BindView(R.id.coordinatortablayout)
    CoordinatorTabLayout mCoordinatorTabLayout;


    List<NewsFragment> fragmentList=new ArrayList<>();

    @Override
    public int setLayoutResId() {
        return R.layout.activity_news;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            addFragment();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return channels[position];
            }
        });
        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());

        int mColorArray[] = new int[]{
                R.color.colorPrimary,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light};
        int mImageArrag[]=new int[]{
                R.mipmap.news,
                R.mipmap.news,
                R.mipmap.news,
                R.mipmap.news
        };
        mCoordinatorTabLayout
                .setTitle("Ccapton News")
                .setImageArray(mImageArrag)
                .setTransulcentStatusBar(this)
                .setContentScrimColorArray(mColorArray)
                .setBackEnable(true)
                .setupWithViewPager(viewPager)
        .setLoadHeaderImagesListener(new LoadHeaderImagesListener() {
            @Override
            public void loadHeaderImages(ImageView imageView, TabLayout.Tab tab) {

                setOnChannelSelectedListener(fragmentList.get(tab.getPosition()));
                if(onChannelSelectedListener!=null)
                    onChannelSelectedListener.onChannelSelected(imageView,0);
            }
        });
    }

    private void addFragment() throws UnsupportedEncodingException {
        for (int i = 0; i <channels.length; i++) {
            NewsFragment fragment=new NewsFragment();
            Bundle bundle=new Bundle();
            bundle.putString("channel",channels[i]);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
    }


    @Override
    public void setListenr() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private OnChannelSelectedListener onChannelSelectedListener;
    public void setOnChannelSelectedListener(OnChannelSelectedListener onChannelSelectedListener){
        this.onChannelSelectedListener=onChannelSelectedListener;
    }
    public interface OnChannelSelectedListener{
        void onChannelSelected(ImageView imageView,int position);
    }


}
