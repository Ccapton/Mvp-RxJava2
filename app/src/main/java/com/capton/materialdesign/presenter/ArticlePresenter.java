package com.capton.materialdesign.presenter;

import android.view.View;

import com.capton.materialdesign.model.article.ArticleModel;
import com.capton.materialdesign.view.IView;

/**
 * Created by capton on 2017/8/30.
 */

public class ArticlePresenter extends IPresenter{

    public ArticlePresenter(IView iView){
        super(iView);
        this.iModel=new ArticleModel();
    }

    @Override
    public void setDataOnView(View view) {
        iView.setDataOnView(ArticlePresenter.this,view);
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
