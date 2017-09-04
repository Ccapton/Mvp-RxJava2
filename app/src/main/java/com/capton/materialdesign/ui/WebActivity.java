package com.capton.materialdesign.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capton.colorfulprogressbar.ColorfulProgressbar;
import com.capton.materialdesign.R;
import com.capton.materialdesign.ui.BaseActivity;
import com.just.library.AgentWeb;

import java.lang.reflect.Method;

import butterknife.BindView;


public class WebActivity extends BaseActivity {

    @BindView(R.id.webViewParent) LinearLayout webViewParent;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.colorfulProgressbar) ColorfulProgressbar progressbar;
    @BindView(R.id.main_content) LinearLayout main_content;
    AgentWeb mAgentWeb;
    WebView mWebView;
    private String url;

    private boolean isFromMyWebSite;
    private final static String TAG="WebActivity";

    @Override
    public int setLayoutResId() {
        return R.layout.activity_web;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector=new GestureDetector(this,onGestureLister);

        url=getIntent().getStringExtra("url");
        if("".equals(url)||url==null){
            Log.i("WebActivity", "url is empty");
            url=getIntent().getData().toString();
        }

        if(url.startsWith(getString(R.string.webSite))) {
            isFromMyWebSite = true;
            progressbar.setVisibility(View.GONE);
        }
        Log.i("WebActivity", "url = "+url);
        mAgentWeb=AgentWeb.with(this)//传入Activity
                .setAgentWebParent(webViewParent, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .closeProgressBar()
                .createAgentWeb()
                .ready()
                .go(url);
        mWebView=mAgentWeb.getWebCreator().get();
        mWebView.setWebChromeClient(mWebChromeClient);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("加载中...");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoadFinished){
                    finish();
                }else {
                    mWebView.stopLoading();
                }
            }
        });
      /* mWebView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               gestureDetector.onTouchEvent(event);
               return false;
           }
       });*/
    }

    @Override
    public void setListenr() {

    }

    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureLister=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    private boolean isLoadFinished;


    private WebChromeClient mWebChromeClient=new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.i(TAG, "onProgressChanged: newProgerss="+newProgress);
            if(newProgress!=100) {
                if(!isFromMyWebSite) {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }
                toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
                isLoadFinished=false;
            }else {
                progressbar.setVisibility(View.GONE);
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                isLoadFinished=true;
            }
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            toolbar.setTitle(title);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_more_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_refresh:
                mWebView.reload();
                break;
            case R.id.toolbar_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.toolbar_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(url);
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_openinbroswer:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    };

    @Override
    public void onBackPressed() {
       if(mWebView.canGoBack()){
           mWebView.goBack();
       }else {
           finish();
       }
    }
    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
