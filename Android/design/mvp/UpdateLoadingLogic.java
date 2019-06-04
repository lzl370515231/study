package com.van.eba.dialog.UpdateLoading;

import android.content.Context;

import com.van.eba.constant.Constants;
import com.van.eba.http.xutils.FileCallBack;
import com.van.eba.http.xutils.HttpManager;
import com.van.eba.model.VanException;
import com.van.eba.ui.App;

import java.io.File;

/**
 *  下载apk对话框 ---- 逻辑层
 * Created by zhangliao on 2016/9/27.
 */
public class UpdateLoadingLogic {

    private Context context;

    private IUpdateLoadingDialog iUpdateLoadingDialog;

    public UpdateLoadingLogic(Context context,IUpdateLoadingDialog iUpdateLoadingDialog){
        this.context = context;
        this.iUpdateLoadingDialog = iUpdateLoadingDialog;
    }

    /**
     * 下载apk
     */
    public void dowmLoadApk(String downUrl){
        String filepath = Constants.SDPATH;
        HttpManager.getInstance(App.getApp(context)).downloadProgressFile(downUrl, filepath, null, new FileCallBack() {
            @Override
            public void onSuccess(File file) {
                iUpdateLoadingDialog.openFile(file);
                iUpdateLoadingDialog.closeDialog();
            }

            @Override
            public void onLoading(long l, long l1) {
                iUpdateLoadingDialog.updateProgrssBar(l,l1);
            }

            @Override
            public void onErrer(VanException e) {
                iUpdateLoadingDialog.showMsg(e.getMsg());
                iUpdateLoadingDialog.closeDialog();
            }
        });
    }
}
