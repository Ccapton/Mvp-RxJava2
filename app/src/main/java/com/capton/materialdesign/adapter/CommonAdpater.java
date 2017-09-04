package com.capton.materialdesign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by capton on 2017/9/1.
 */

public abstract class CommonAdpater <T> extends RecyclerView.Adapter {

    private List <T> mList;
    private Context mContext;
    private int mLayoutId;

    public List  getDataList() {
        return mList;
    }

    public void setDataList(List mList) {
        this.mList = mList;
    }

    public CommonAdpater(Context mContext,List<T> mList) {
        this.mList = mList;
        this.mContext = mContext;
     //   this.mLayoutId = layoutId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(getLayoutId(viewType),parent,false);
        RecyclerView.ViewHolder viewHolder= new CommonViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public abstract int getLayoutId(int viewType);

    @Override
    public abstract int getItemViewType(int position);

    class CommonViewHolder extends RecyclerView.ViewHolder{

        public CommonViewHolder(View itemView) {
            super(itemView);
        }
    }

}
