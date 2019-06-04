package com.van.eba.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.komi.slider.ISlider;
import com.komi.slider.SliderUtils;
import com.van.eba.R;
import com.van.eba.constant.BroadCastActions;
import com.van.eba.tools.DialogUtil;
import com.van.eba.ui.App;

/**
 * 对话框 --- 抽象基类
 * 作者：zhangliao
 */
public abstract class VanDialog extends DialogFragment{

    public static final String KEY_TITLE = "title";
    public static final String KEY_VALUE = "value";

    private boolean isCancel = true;

    private ISlider iSlider;

    /**
     * 右滑返回
     */
    public void showBackAction(){
        //退出动画
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_back;

        App app = (App)getActivity().getApplicationContext();
        iSlider = SliderUtils.attachDialog(getActivity(),getDialog(),app.getSliderConfig());
        setSliderEnable(false);
    }

    public void setCancel(boolean b){
        this.isCancel = b;
        if (null != getDialog())
        {
            getDialog().setCancelable(isCancel);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }
    /**
     * 设置滑动是否可用
     * @param isLock
     */
    public void setSliderEnable(boolean isLock){
        if(iSlider == null){
            return;
        }
        //解锁
        if(isLock){
            iSlider.unlock();
        }else{
            iSlider.lock();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != getDialog())
        {
            getDialog().setCancelable(isCancel);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    /**
     * 实例化
     * @param c
     * @param bd
     * @return
     */
    public static final <E extends VanDialog > E newInstance(Class<E> c , Bundle bd){
        try{
            E f = c.newInstance();
            f.setArguments(bd);
            return f;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final <E extends View> E getView (View view, int id) {
        try {
            return (E)view.findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

    /**
     * 根据百分比设置窗口大小
     * @param widthCent
     * @param heightCent
     */
    public void setSizePercent(float widthCent,float heightCent){
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getActivity().getResources().getDisplayMetrics(); // 获取屏幕宽、高

        if(widthCent != 1){
            lp.width = (int) (d.widthPixels * widthCent);
        }else{
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        if(heightCent != 1){
            lp.height = (int) (d.widthPixels * heightCent);
        }else{
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        dialogWindow.setAttributes(lp);
    }

    /**
     * 开启全屏
     */
    public void setFull(){
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if(manager.isDestroyed()){
            return;
        }

        super.show(manager, tag);
        setStyle(STYLE_NO_FRAME,R.style.popWindowStyle);
    }

    public void register(){
        // 注册广播接收
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastActions.UPDATE_SLIDER);    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);
    }

    public void unRegister(){
        getActivity().unregisterReceiver(receiveBroadCast);
    }

    public BroadcastReceiver receiveBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSliderEnable(true);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void dismiss() {
        if(iSlider != null){
            iSlider.slideExit();
        }
        super.dismiss();
    }

    /**
     * 提出提示框
     * @param msg
     */
    public void showMsgDialog(String msg){
        if( !isAdded()){
            return;
        }

        DialogUtil.showDialog(getActivity(),msg);
    }


}
