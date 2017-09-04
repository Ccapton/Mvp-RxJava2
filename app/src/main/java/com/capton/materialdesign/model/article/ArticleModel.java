package com.capton.materialdesign.model.article;

import com.capton.materialdesign.model.IModel;
import com.capton.materialdesign.model.news.News;
import com.capton.materialdesign.model.news.NewsContentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by capton on 2017/8/30.
 */

public class ArticleModel implements IModel {

    private Article mArticle=new Article();

    @Override
    public void set(Object object) {
        mArticle= (Article) object;
    }

    @Override
    public  Object  get() {
        return mArticle;
    }

}
