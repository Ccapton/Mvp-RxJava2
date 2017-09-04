package com.capton.materialdesign.presenter;

import android.view.View;

import com.capton.materialdesign.model.IModel;
import com.capton.materialdesign.view.IView;

/**
 * IPresenter与IModel、IView不同，这是一个抽象类，内含IModel、IView子类对象。用户新建对象后，等待后续操作
 * Created by capton on 2017/9/3.
 */

public abstract class IPresenter{
    IView<IPresenter> iView;  //Activity，Fragment实现此接口，等待IPresenter子类对象 回调 setDataOnView()方法
    IModel iModel;            //数据模型，不暴露给Activity，Fragment等IView子类
    /**
     * 以IView子类作为参数的构造函数
     * @param iView 例如，Activity.this
     */
    public IPresenter(IView<IPresenter> iView){
        this.iView=iView;
    }

    /**
     * 传入view，IPresenter子类将从Model层中获取Bean数据，再加载到view中（用于ListView,RecycerView等列表视图）
     * @param view
     */
    public abstract void setDataOnView(View view);

    /**
     * 设置Bean数据到Model层的IModel子类中去
     * @param object Bean数据对象，例如Article对象
     */
    public abstract void saveData(Object object);

    /**
     * 从Model层获取Bean数据
     * @return
     */
    public abstract Object getData();
}
