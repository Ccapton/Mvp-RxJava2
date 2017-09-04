package com.capton.materialdesign.model.article;

import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by capton on 2017/8/31.
 */

public class XmlPull {


    public static List<Article> getArticles(String xmlString) throws Exception {

        return pull2xml(new ByteArrayInputStream(xmlString.getBytes()));
    }


    public static List<Article> pull2xml(InputStream is) throws Exception {
        List<Article> list = null;
        Article article = null;
        String temp="";
        String author="";
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    if ("feed".equals(parser.getName())) {
                        list = new ArrayList<>();
                    } else if("name".equals(parser.getName())){
                        author=parser.nextText();
                    } else if ("entry".equals(parser.getName())) {
                        article = new Article();
                        article.setAuthor(author);
                    } else if ("title".equals(parser.getName())) {
                        String title = parser.nextText();
                        if(article!=null) {
                            article.setTitle(title);
                        }
                    } else if ("id".equals(parser.getName())) {
                        String id = parser.nextText();
                        if(article!=null) {
                            article.setId(id);
                        }
                    } else if("published".equals(parser.getName())){
                        String published=parser.nextText() ;
                        published= TextUtils.substring(published,0,10);
                        article.setDate(published);
                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("entry".equals(parser.getName())) {
                        list.add(article);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        return list;
    }

}
