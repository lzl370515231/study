package com.lzl.builder.android.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME){

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到指定Activity
     * @param fromActivity
     * @param toActivity
     */
    public void toActivity(BaseActivity fromActivity,BaseActivity toActivity){

    }

    /**
     * 添加某个Activity
     * @param activity
     */
    protected void addActivity(BaseActivity activity){

    }

    /**
     * 从栈中删除某个Activity
     */
    protected void removeActivity(BaseActivity activity){

    }

    /**
     * 清空Activity栈
     */
    protected void clearActivity(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
