package com.van.eba.dialog.Update;

/**
 * 欢迎页面回调
 * Created by zhangliao on 2016/11/7.
 */
public interface IUpdateCheckAction {

    /**
     * 强制更新时不更新
     */
    void enforceExit();

    /**
     * 非强制更新时取消按钮
     */
    void cancelAction();

    /**
     * 设置角标，如果需要
     */
    void setBadgeNumber();


}
