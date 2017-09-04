package com.capton.materialdesign.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.capton.materialdesign.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tv_copyright) TextView tvCopyright;
    @BindView(R.id.iv_splash) ImageView splashIv;

    @BindArray(R.array.loading) String loadingImageUrls[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        super.onCreate(savedInstanceState);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText(getString(me.wcy.music.R.string.copyright, year));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(loadingImageUrls[(int) (Math.random()*loadingImageUrls.length)]);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<String>() {
              @Override
              public void onSubscribe(@NonNull Disposable d) {}
              @Override
              public void onNext(@NonNull String s) {
                  Glide.with(SplashActivity.this).load(s).fitCenter().into(splashIv);
              }

              @Override
              public void onError(@NonNull Throwable e) {
                  startActivity(new Intent(SplashActivity.this,MainActivity.class));
                  finish();
              }

              @Override
              public void onComplete() {
                  Observable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                      @Override
                      public void accept(Long aLong) throws Exception {
                          startActivity(new Intent(SplashActivity.this,MainActivity.class));
                          finish();
                      }
                  });
              }
          });
    }

    @Override
    public void setListenr() {

    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_splash_app;
    }
}
