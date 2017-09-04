package com.capton.materialdesign.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.capton.materialdesign.R;
import com.capton.materialdesign.adapter.CommonAdpater;
import com.capton.materialdesign.model.article.XmlPull;
import com.capton.materialdesign.model.news.News;
import com.capton.materialdesign.model.news.NewsContentModel;
import com.capton.materialdesign.presenter.NewsContentPresenter;
import com.capton.materialdesign.ui.BaseFragment;
import com.capton.materialdesign.view.IView;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindAnim;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by capton on 2017/9/3.
 */

public class NewsFragment extends BaseFragment implements IView<NewsContentPresenter>,NewsActivity.OnChannelSelectedListener {

    @BindView(R.id.recyclerView2) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout2) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.toTopBtn) FloatingActionButton toTopBtn;

    @BindString(R.string.jisu_api) String URL;
    @BindString(R.string.jisu_appkey) String APPKEY;

      AnimatorSet mHideFAB;
      AnimatorSet mShowFAB;

    private String url;
    private SharedPreferences spf;
    private String channel;

    private int start=0;
    private int num=6;
    private List<News.NewsContent> newsContents;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spf=getActivity().getSharedPreferences("News", Context.MODE_PRIVATE);
        channel=getArguments().getString("channel");
        start=spf.getInt("News_start_"+channel,0);
        try {
            url = URL + "?channel=" + URLEncoder.encode(channel, "utf-8") +"&start="+start+ "&num=" + num + "&appkey=" + APPKEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutResId(),container,false);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                addDataToRecyelerView(true);
            }
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                addDataToRecyelerView(false);
            }
        });
        requestData();
        toTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
              if(dy>50){
                  if(!isHided)
                  hideFAB();
              }
              if(dy<-50){
                  if(isHided)
                  showFAB();
              }
            }
        });
        toTopBtn.setImageResource(R.drawable.ic_arrow_upward_white_36dp);
        initAnimation();
        return view;
    }

    private void initAnimation(){
                 mHideFAB = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),R.anim.scroll_hide_fab);
                 mShowFAB = (AnimatorSet)AnimatorInflater.loadAnimator(getContext(),R.anim.scroll_show_fab);
                 mHideFAB.setTarget(toTopBtn);
                 mShowFAB.setTarget(toTopBtn);
             }

             boolean isHided;
    private void hideFAB() {
        mHideFAB.start();
        isHided=true;
    }
     private void showFAB(){
         mShowFAB.start();
         isHided=false;
     }


    private void addDataToRecyelerView(final boolean isloadMore){

        start+=6;
        try {
            url = URL + "?channel=" + URLEncoder.encode(channel, "utf-8") +"&start="+start+ "&num=" + num + "&appkey=" + APPKEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (NetworkUtils.isConnected()) {
            requestNetWorkData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            News news=new Gson().fromJson(s,News.class);
                            if(newsContents==null)
                                newsContents=news.getResult().getList();
                            if(!isloadMore)
                               newsContents.addAll(0,news.getResult().getList());
                            else
                                newsContents.addAll(news.getResult().getList());
                            if(recyclerView.getAdapter()==null) {
                                recyclerView.setAdapter(new CommonAdpater<News.NewsContent>(getActivity(), newsContents) {
                                    @Override
                                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                        NewsContentPresenter contentPresenter = new NewsContentPresenter(NewsFragment.this);
                                        contentPresenter.saveData(newsContents.get(position));
                                        if("".equals(newsContents.get(position).getPic())
                                                ||newsContents.get(position).getPic()==null
                                                ){
                                            newsContents.get(position).setType(1);
                                        }
                                        contentPresenter.setDataOnView(holder.itemView);
                                    }
                                    @Override
                                    public int getLayoutId(int viewType) {
                                        if (viewType == 0) {
                                            return R.layout.news_item1;
                                        } else {
                                            return R.layout.news_item2;
                                        }
                                    }

                                    @Override
                                    public int getItemViewType(int position) {
                                        return newsContents.get(position).getType();
                                    }
                                });
                            }
                            recyclerView.getAdapter().notifyDataSetChanged();
                            spf.edit().putString("News_"+channel,s).apply();
                            spf.edit().putInt("News_start_"+channel,start).apply();
                            mRefreshLayout.finishLoadmore();
                            mRefreshLayout.finishRefresh();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            mRefreshLayout.finishLoadmore();
                            mRefreshLayout.finishRefresh();
                        }
                    });
        }else {
            mRefreshLayout.finishLoadmore();
            mRefreshLayout.finishRefresh();
            Toast.makeText(getActivity(),"网路未连接",Toast.LENGTH_SHORT).show();
        }
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
                        System.out.println("News DATA: " + s);

                        News news=new Gson().fromJson(s,News.class);
                        newsContents=news.getResult().getList();

                        recyclerView.setAdapter(new CommonAdpater<News.NewsContent>(getActivity(),newsContents){
                            @Override
                            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                                NewsContentPresenter contentPresenter=new NewsContentPresenter(NewsFragment.this);
                                contentPresenter.saveData(newsContents.get(position));
                                if("".equals(newsContents.get(position).getPic())
                                        ||newsContents.get(position).getPic()==null
                                        ){
                                    newsContents.get(position).setType(1);
                                }
                                contentPresenter.setDataOnView(holder.itemView);
                            }

                            @Override
                            public int getLayoutId(int viewType) {
                                if(viewType==0) {
                                    return R.layout.news_item1;
                                }else {
                                    return R.layout.news_item2;
                                }
                            }
                            @Override
                            public int getItemViewType(int position) {
                                return newsContents.get(position).getType();
                            }

                        });
                        recyclerView.getAdapter().notifyDataSetChanged();
                        spf.edit().putString("News_"+channel,s).apply();
                        spf.edit().putInt("News_start_"+channel,start).apply();
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        spf.edit().putString("News_"+channel,"").apply();
                        spf.edit().putInt("News_start_"+channel,start).apply();
                        mRefreshLayout.finishLoadmore();
                        mRefreshLayout.finishRefresh();
                    }
                    @Override
                    public void onComplete() {
                        mRefreshLayout.finishLoadmore();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }
    private Observable<String> requestLocalData(){
        return  Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if(!spf.getString("News_"+channel,"").equals("")){
                    e.onNext(spf.getString("News_"+channel,""));
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
                Request request=new  Request.Builder().get().url(url).build();
                Call call=new OkHttpClient().newCall(request);
                Response response=call.execute();
                if(response.isSuccessful()){
                    e.onNext(response.body().string());
                    e.onComplete();
                }else {
                    e.onError(new Throwable("Failed to get News data!"));
                }
            }
        });
    }

    @Override
    public void onChannelSelected(ImageView imageView, int position) {
        if(newsContents.get(position).getPic()!=null||!newsContents.get(position).getPic().equals(""))
            Glide.with(this).load(newsContents.get(position).getPic()).into(imageView);
        else
            Glide.with(this).load(R.mipmap.news).into(imageView);
    }
    private void startActivity(Class targetActivityClass,String param){
        Intent intent=new Intent(getActivity(),targetActivityClass);
        if(param!=null)
            intent.putExtra("url",param);
        startActivity(intent);
    }

    @Override
    public void setDataOnView(final NewsContentPresenter newsContentPresenter, View view) {
        TextView titleView= (TextView) view.findViewById(R.id.titleTv);
        TextView contentView= (TextView) view.findViewById(R.id.contentTv);
        ImageView newsIView= (ImageView) view.findViewById(R.id.newsIv);
        titleView.setText(((News.NewsContent)newsContentPresenter.getData()).getTitle());
        contentView.setText(((News.NewsContent)newsContentPresenter.getData()).getSrc()+" "
                +((News.NewsContent)newsContentPresenter.getData()).getTime());
        if(newsIView!=null)
            Glide.with(getContext()).load(((News.NewsContent)newsContentPresenter.getData()).getPic()).override(200,138).into(newsIView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebActivity.class,((News.NewsContent)newsContentPresenter.getData()).getUrl());
            }
        });
    }

}
