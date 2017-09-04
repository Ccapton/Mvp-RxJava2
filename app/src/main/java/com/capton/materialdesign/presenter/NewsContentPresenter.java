package com.capton.materialdesign.presenter;

import android.view.View;

import com.capton.materialdesign.model.IModel;
import com.capton.materialdesign.model.news.News;
import com.capton.materialdesign.model.news.NewsContentModel;
import com.capton.materialdesign.view.IView;

/**
 * Created by capton on 2017/9/3.
 */

public  class NewsContentPresenter extends IPresenter{


    public NewsContentPresenter(IView iView) {
        super(iView);
        this.iModel=new NewsContentModel();
    }
    @Override
    public void setDataOnView(View view) {
        iView.setDataOnView(NewsContentPresenter.this,view);
    }

    @Override
    public void saveData(Object object) {
        iModel.set(object);
    }

    @Override
    public Object getData() {
        return this.iModel.get();
    }
}
