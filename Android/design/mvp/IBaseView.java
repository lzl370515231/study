package com.van.eba.interfaces;

/**
 * 所有 P 层的基类，
 * 在这里可以定义通用的接口
 * Created by zhangliao on 2016/10/20.
 */
public interface IBaseView {

    /**
     * 弹出一个对话框
     * @param msg
     */
    void showMsgDialog(String msg);
}
