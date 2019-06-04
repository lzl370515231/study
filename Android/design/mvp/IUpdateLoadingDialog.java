package com.van.eba.dialog.UpdateLoading;

import java.io.File;

/**
 * 下载对话框进度条接口
 * Created by zhangliao on 2016/9/27.
 */
public interface IUpdateLoadingDialog {

    void updateProgrssBar(float total, float count);

    void openFile(File file);

    void showMsg(String msg);

    void closeDialog();

}
