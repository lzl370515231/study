package com.van.eba.dialog.UpdateLoading;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.van.eba.R;
import com.van.eba.tools.DialogUtil;
import com.van.eba.tools.FileUtils;
import com.van.eba.tools.L;
import com.van.eba.tools.T;
import com.van.eba.tools.Utils;
import com.van.eba.widget.VanDialog;

import java.io.File;

/**
 * 下载apk对话框
 * Created by zhangliao on 2016/9/22.
 */
public class UpdateLoadingDialog extends VanDialog implements IUpdateLoadingDialog,DialogInterface.OnKeyListener{

    private View view;

    private ProgressBar bar;

    private UpdateLoadingLogic logic;

    /** 下载url */
    private String downUrl = "";

    private String md5Code = "";

    public void setMd5Code(String md5Code){
        this.md5Code = md5Code;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.update_loading_dialog,null);
        bar = getView(view,R.id.update_loading_dialog_pb);

        //必须要这样按返回按钮才能监听到
        getDialog().setOnKeyListener(this);
        logic = new UpdateLoadingLogic(getActivity(),this);

        if(!downUrl.equalsIgnoreCase("")){
            logic.dowmLoadApk(downUrl);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setSizePercent(0.9f,1);
    }

    @Override
    public void updateProgrssBar(float total, float count) {
        bar.setMax((int)total);
        bar.setProgress((int)count);
    }

    @Override
    public void openFile(File file) {
        //如果MD5值一样
        String localMD5 = Utils.getSignMd5Str(getActivity());
//        L.i("安装包签名对比---> "+localMD5 +" === "+md5Code);
        if(localMD5.equalsIgnoreCase(md5Code)){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }else{
            showMsg(getString(R.string.apk_error));
        }
    }

    @Override
    public void showMsg(final String msg) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.showDialog(getActivity(),msg);
                }
            });
        }
    }

    @Override
    public void closeDialog() {
        dismiss();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
        return keyCode == KeyEvent.KEYCODE_BACK;
    }
}
