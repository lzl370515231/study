package com.van.eba.ui.login;

import com.van.eba.interfaces.IBaseView;

/**
 * 登陆接口
 * 作者：zhangliao
 * 时间：2016/9/6 15:17
 */
public interface ILoginAct extends IBaseView {

    void loginSuc();

    /**
     * 保存账号
     */
    void saveAccount();

}
