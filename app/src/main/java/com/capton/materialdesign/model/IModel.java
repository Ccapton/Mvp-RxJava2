package com.capton.materialdesign.model;

/**
 * Created by capton on 2017/9/3.
 */

/**
 *  Model层接口
 */
public interface IModel{
    /**
     *   设置Bean数据
     * @param object
     */
    void set(Object object);

    /**
     *   获取Bean数据
     * @return
     */
    Object get();
}
