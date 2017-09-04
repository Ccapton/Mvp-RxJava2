package com.capton.materialdesign.model.news;

import com.capton.materialdesign.model.IModel;
import com.capton.materialdesign.model.news.News.NewsContent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.value;

/**
 * Created by capton on 2017/9/3.
 */

public class NewsContentModel implements IModel{

    private NewsContent newsContent=new NewsContent();
    @Override
    public void set(Object object) {
        newsContent= (NewsContent) object;
    }

    @Override
    public  Object  get() {
        return newsContent;
    }


}
