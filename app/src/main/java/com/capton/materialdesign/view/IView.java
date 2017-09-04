package com.capton.materialdesign.view;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by capton on 2017/8/30.
 */

public interface IView<IPresenter>{
    /**
     * 等待Present调用的方法，子类（例如Activity，Fragment）实现其具体代码
     * @param iPresenter 返回的iPresenter本体对象
     * @param view    你要加载的具体视图，例如在RecyclerView中，onBindViewHolder方法里的viewHolder.itemView;
     */
    void setDataOnView(IPresenter iPresenter,View view);
}
