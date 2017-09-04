package com.capton.materialdesign.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.capton.materialdesign.R;
import com.capton.materialdesign.adapter.CommonAdpater;
import com.capton.materialdesign.model.article.Article;
import com.capton.materialdesign.model.article.ArticleModel;
import com.capton.materialdesign.model.article.XmlPull;
import com.capton.materialdesign.presenter.ArticlePresenter;
import com.capton.materialdesign.util.MemoryUtil;
import com.capton.materialdesign.view.IView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindAnim;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.wcy.music.activity.SplashActivity;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import online.osslab.CircleProgressBar;


public class MainActivity extends BaseActivity implements IView<ArticlePresenter>,View.OnClickListener{

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)  SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imageView) ImageView imageView ;
    @BindView(R.id.drawerlayout) DrawerLayout drawerlayout ;
    @BindView(R.id.meIv) CircleImageView meIv ;
    @BindView(R.id.nameTv) TextView nameTv ;
    @BindView(R.id.memoryText) TextView memoryTv ;
    @BindView(R.id.memoryStateBar) CircleProgressBar memoryStateBar;
    @BindView(R.id.startMusicLayout) LinearLayout startMusicLayout;
    @BindView(R.id.startNewsLayout) LinearLayout startNewsLayout;
    @BindView(R.id.startBlogLayout) LinearLayout startBlogLayout;
    @BindView(R.id.startGithubLayout) LinearLayout startGithubLayout;
    @BindView(R.id.floatingBtn) FloatingActionButton floatingActionBtn;

    @BindAnim(R.anim.left_in) Animation leftInAnimation;
    @BindAnim(R.anim.right_in) Animation rightInAnimation;

    private final static String TAG="MainActivity";

    private List<Article> articles;
    private SharedPreferences spf;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spf=getSharedPreferences("History",MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//可不加
        }

        collapsingToolbarLayout.setTitle("Ccapton");
        collapsingToolbarLayout.setExpandedTitleColor(Color.DKGRAY);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(Gravity.START,true);
                leftInAnimation.setInterpolator(new BounceInterpolator());
                rightInAnimation.setInterpolator(new BounceInterpolator());
                meIv.startAnimation(leftInAnimation);
                nameTv.startAnimation(rightInAnimation);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.shashengwan);
        Bitmap gaussianBitmap=GaussianBlurImageProcessor.fastGaussianBlur(bitmap,20,false);
        imageView.setImageBitmap(gaussianBitmap);

        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       Toast.makeText(MainActivity.this,"更多内容请访问我的主页",Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishLoadmore(true);
                    }
                },1500);
            }
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
               if (NetworkUtils.isConnected()) {
                   requestNetWorkData()
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Consumer<String>() {
                               @Override
                               public void accept(String s) throws Exception {
                                   Log.i(TAG, "XML DATA: " + s);
                                   try {
                                       articles = XmlPull.getArticles(s);
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                     /*
                               *  设置展示类型（因为数据源是rss返回的xml文件解析而来，没有type属性，这里手动设置要重点展示的项）
                               * */
                                   for (int i = 0; i <articles.size(); i++) {
                                       if(i==1||i==3||i==5||i==7||i==8||i==0){
                                           articles.get(i).setType(1);
                                       }else {
                                           articles.get(i).setType(0);
                                       }
                                   }
                                   recyclerView.setAdapter(new MyAdapter(MainActivity.this,articles));
                                   recyclerView.getAdapter().notifyDataSetChanged();
                                   spf.edit().putString("Articles", s).apply();
                                   mRefreshLayout.finishLoadmore(true);
                                   mRefreshLayout.finishRefresh(true);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   throwable.printStackTrace();
                                   mRefreshLayout.finishLoadmore(true);
                                   mRefreshLayout.finishRefresh(true);
                               }
                           });
               }else {
                   mRefreshLayout.finishLoadmore(true);
                   mRefreshLayout.finishRefresh(true);
                   Toast.makeText(MainActivity.this,"网路未连接",Toast.LENGTH_SHORT).show();
               }
            }
        });

        showMemoryState();
        requestData();
    }

    @Override
    public void setListenr() {
        meIv.setOnClickListener(this);
        floatingActionBtn.setOnClickListener(this);
        startMusicLayout.setOnClickListener(this);
        startNewsLayout.setOnClickListener(this);
        startBlogLayout.setOnClickListener(this);
        startGithubLayout.setOnClickListener(this);
    }

    private void showMemoryState() {
        Observable.interval(0,5,TimeUnit.SECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    int total=(int) MemoryUtil.getInstance(MainActivity.this).getTotalMemoryNumber();
                    int free=(int) MemoryUtil.getInstance(MainActivity.this).getAvailMemoryNumber();
                    float partition=(float)free/total;
                    memoryStateBar.setProgressWithAnimation((1-partition)*100,4500);
                    memoryTv.setText("可用内存/总内存："+MemoryUtil.getInstance(MainActivity.this).getAvailMemory()+
                          "/"+MemoryUtil.getInstance(MainActivity.this).getTotalMemory());
              }
          });
    }

    private AlertDialog dialog(){
            AlertDialog  alertDialog = new AlertDialog.Builder(this).create();
            View dialogView = View.inflate(this, R.layout.about_me_dialog_latyout, null);
            TextView aboutTv = (TextView) dialogView.findViewById(R.id.aboutTv);
            Button sendMailBtn = (Button) dialogView.findViewById(R.id.sendMailBtn);
            Window window = alertDialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setView(dialogView, 50, 50, 50, 50);
            aboutTv.append(getString(R.string.aboutMe) + "\n");
            aboutTv.append("微信号：Ccapton");
            sendMailBtn.setOnClickListener(this);
        return alertDialog;
    }

    private void requestData(){
        Observable.concat(requestLocalData(),requestNetWorkData())
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Observer<String>() {
                       @Override
                       public void onSubscribe(@NonNull Disposable d) {}

                       @Override
                       public void onNext(@NonNull String s) {
                              System.out.println("XML DATA: " + s);
                               try {
                                   articles = XmlPull.getArticles(s);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                               /*
                               *  设置展示类型（因为数据是rss返回的xml文件解析而来，没有type属性，这里手动设置要重点展示的项）
                               * */
                           for (int i = 0; i <articles.size(); i++) {
                               if(i==1||i==3||i==5||i==7||i==8||i==0){
                                   articles.get(i).setType(1);
                               }else {
                                   articles.get(i).setType(0);
                               }
                           }
                              mRefreshLayout.finishRefresh(true);
                               recyclerView.setAdapter(new MyAdapter(MainActivity.this,articles));
                               recyclerView.getAdapter().notifyDataSetChanged();
                               spf.edit().putString("Articles",s).apply();
                       }
                       @Override
                       public void onError(@NonNull Throwable e) {
                           e.printStackTrace();
                           spf.edit().putString("Articles","").apply();
                           mRefreshLayout.finishLoadmore();
                           mRefreshLayout.finishRefresh();
                       }
                       @Override
                       public void onComplete() {
                           mRefreshLayout.finishLoadmore(true);
                           mRefreshLayout.finishRefresh(true);
                       }
                   });
    }
    private Observable<String> requestLocalData(){
      return  Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if(!spf.getString("Articles","").equals("")){
                    e.onNext(spf.getString("Articles",""));
                }else {
                    e.onComplete();
                }
            }
        });
    }

    private Observable<String> requestNetWorkData(){
        return  Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Request request=new  Request.Builder().get().url(getString(R.string.rss)).build();
                Call call=new OkHttpClient().newCall(request);
                Response response=call.execute();
                 if(response.isSuccessful()){
                    e.onNext(response.body().string());
                    e.onComplete();
                 }else {
                     e.onError(new Throwable("Failed to get xml data!"));
                 }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.article_item:

                break;
            case R.id.meIv:
                dialog().show();
                break;
            case R.id.sendMailBtn:
              sendEmail();
                break;
            case R.id.startMusicLayout:
                startActivity(SplashActivity.class,null);
                break;
            case R.id.startNewsLayout:
                startActivity(NewsActivity.class,null);
                break;
            case R.id.startBlogLayout:
                startActivity(WebActivity.class,getString(R.string.webSite));
                break;
            case R.id.startGithubLayout:
                startActivity(WebActivity.class,getString(R.string.github));
                break;
            case R.id.floatingBtn:
                startActivity(WebActivity.class,getString(R.string.github));
                break;
        }
    }

    private void startActivity(Class targetActivityClass,String param){
        Intent intent=new Intent(this,targetActivityClass);
        if(param!=null)
        intent.putExtra("url",param);
        startActivity(intent);
    }

    private void sendEmail() {
        Uri uri = Uri.parse("mailto:"+getString(R.string.email));
        String[] email = {getString(R.string.email)};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "你好，陈尉斌");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, "选择邮件客户端"));
    }

    @Override
    public void setDataOnView(final ArticlePresenter presenter, View view) {
        TextView view1= (TextView) view.findViewById(R.id.title);
        TextView view2= (TextView) view.findViewById(R.id.author);
        TextView view3= (TextView) view.findViewById(R.id.date);
        view1.setText(((Article)presenter.getData()).getTitle());
        view2.setText(((Article)presenter.getData()).getAuthor());
        view3.setText(((Article)presenter.getData()).getDate());
        RelativeLayout article_item = (RelativeLayout) view.findViewById(R.id.article_item);
        article_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebActivity.class,((Article)presenter.getData()).getId());
            }
        });
    }


    class MyAdapter extends  CommonAdpater<Article>{

        public MyAdapter(Context mContext, List<Article> mList) {
            super(mContext, mList);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ArticlePresenter presenter=new ArticlePresenter(MainActivity.this);
            presenter.saveData(articles.get(position));
            presenter.setDataOnView(holder.itemView);
        }
        @Override
        public int getLayoutId(int viewType) {
            if(viewType==1) {
                return R.layout.article_item_layout;
            }else {
                return R.layout.article_item_layout2;
            }
        }
        @Override
        public int getItemViewType(int position) {
            return articles.get(position).getType();
        }
    }



}
