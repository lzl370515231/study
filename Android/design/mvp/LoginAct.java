package com.van.eba.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.van.eba.R;
import com.van.eba.dialog.Update.IUpdateCheckAction;
import com.van.eba.dialog.Update.UpdateDialog;
import com.van.eba.model.request.login;
import com.van.eba.tools.DialogUtil;
import com.van.eba.tools.DoubleClickCheck;
import com.van.eba.tools.SPUtils;
import com.van.eba.tools.StringUtils;
import com.van.eba.tools.T;
import com.van.eba.ui.BaseActivity;
import com.van.eba.ui.main.MainAct;
import com.van.eba.ui.main.newmainact.NewMainActivity;
import com.van.eba.widget.Dialog2Btn;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Method;

/**
 * 登陆界面
 */
@ContentView(R.layout.aty_login)
public class LoginAct extends BaseActivity implements ILoginAct, View.OnClickListener {

    /**
     * 标记logo
     */
    @ViewInject(R.id.logo)
    public ImageView logoView;

    /**
     * 用户名输入框
     */
    @ViewInject(R.id.login_name)
    public EditText userNameEd;

    /**
     * 密码输入框
     */
    @ViewInject(R.id.login_pass)
    public EditText passWordEd;

    @ViewInject(R.id.login_btn)
    public Button loginBtn;

    @ViewInject(R.id.login_name_clear_edittext_btn)
    public TextView nameClearTv;

    @ViewInject(R.id.login_pass_clear_edittext_btn)
    public TextView pwClearTv;

    /**
     * 登录逻辑层
     */
    public LoginActLogic logic;

    @Override
    protected void onCreateAction() {
        setShowBackAnim(false);
        initView();
    }

    /**
     * 密码最短
     */
    public static final int pwMin = 6;
    public static final int pwMax = 20;

    private UpdateDialog updateDialog;

    /**
     * 初始化
     */
    private void initView() {
        loginBtn.setOnClickListener(this);
        logic = new LoginActLogic(this, this);
        userNameEd.setText(String.valueOf(SPUtils.get(this, LoginActLogic.KEY_ACCOUNT, "")));
        //软键盘的Enter键默认显示的是“完成”
        passWordEd.setImeOptions(EditorInfo.IME_ACTION_DONE);

        changeView(userNameEd, nameClearTv);
        changeView(passWordEd, pwClearTv);

//        logoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(App.IS_DEBUG){
//                    final String[] url = new String[]{"http://10.1.251.17/","http://10.1.253.82/","http://eba.vanrui.com/"};
//                    int defualtIndex = 0;
//                    for(int i = 0 ; i < url.length ; i++){
//                        if(url[i].equalsIgnoreCase(Constants.HOST_API)){
//                            defualtIndex = i;
//                            break;
//                        }
//                    }
//
//                    DialogUtil.showSingChoiceDialog(LoginAct.this, "选择环境",url , defualtIndex, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Constants.HOST_API = url[which];
//                            T.showShort(LoginAct.this,url[which]);
//                            dialog.dismiss();
//                        }
//                    },null,null);
//                }
//            }
//        });

        nameClearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameEd.requestFocus();
                userNameEd.setText("");
            }
        });

        pwClearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passWordEd.requestFocus();
                passWordEd.setText("");
            }
        });

        userNameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeView(userNameEd, nameClearTv);
            }
        });

        passWordEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeView(passWordEd, pwClearTv);
            }
        });

        updateDialog = new UpdateDialog(this, new IUpdateCheckAction() {
            @Override
            public void enforceExit() {
                LoginAct.this.finish();
            }

            @Override
            public void cancelAction() {
            }

            @Override
            public void setBadgeNumber() {

            }
        });
    }

    /**
     * 改变清楚按钮状态
     *
     * @param editText
     * @param clearTv
     */
    private void changeView(EditText editText, TextView clearTv) {
        if (!StringUtils.isBlank(editText.getText().toString())) {
            clearTv.setVisibility(View.VISIBLE);
        } else {
            clearTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userNameEd.setSelection(userNameEd.getText().toString().length());
        passWordEd.setSelection(passWordEd.getText().toString().length());

        updateDialog.checkUpdateAction(true, false, false);

        setTintColor(R.color.black);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (DoubleClickCheck.isFastClick()) {
            return;
        }

        switch (v.getId()) {
            case R.id.login_btn:
                if (checkInput()) {
                    String deviceType = "";
                    String imei = "";
                    boolean b = checkAppPermission(Manifest.permission.READ_PHONE_STATE);
                    if (b) {
                        deviceType = Build.MODEL;
                        imei = getImeiNew();
                        if (imei == null) {
                            DialogUtil.showDialog(LoginAct.this, "提示", "请先打开睿联读取手机信息的权限 !", "取消", "去设置", new Dialog2Btn.Dialog2BtnLintener() {
                                @Override
                                public void leftAction(DialogFragment dialogFragment) {
                                    dialogFragment.dismiss();
                                }

                                @Override
                                public void rightAction(DialogFragment dialogFragment) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", LoginAct.this.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialogFragment.dismiss();
                                }
                            });
                            return;
                        }
                        login login = new login();
                        login.setLoginDeviceIMEI(imei);
                        login.setLoginDeviceType(deviceType);
                        login.setUserName(userNameEd.getText().toString());
                        login.setPassword(passWordEd.getText().toString());
                        logic.loginAction(login);
                    } else {
                        DialogUtil.showDialog(LoginAct.this, "提示", "请先打开睿联读取手机信息的权限 !", "取消", "去设置", new Dialog2Btn.Dialog2BtnLintener() {
                            @Override
                            public void leftAction(DialogFragment dialogFragment) {
                                dialogFragment.dismiss();
                            }

                            @Override
                            public void rightAction(DialogFragment dialogFragment) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", LoginAct.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialogFragment.dismiss();
                            }
                        });
                    }
                }
                break;
        }
    }

    public String getImeiNew() {
        String imei = null;
        try {
            TelephonyManager var2 = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            if (var2 != null && checkWarrantPermission(Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT >= 26) {
                    try {
                        Method method = var2.getClass().getMethod("getImei", new Class[0]);
                        method.setAccessible(true);
                        imei = (String) method.invoke(var2, new Object[0]);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (TextUtils.isEmpty(imei)) {
                        imei = var2.getDeviceId();
                    }
                } else {
                    imei = var2.getDeviceId();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imei;
    }

    /**
     * 输入检测
     *
     * @return
     */
    private boolean checkInput() {
        if (userNameEd.getText().toString().equalsIgnoreCase("")) {
            userNameEd.requestFocus();
            T.showShort(this, getString(R.string.occount_hint));
            return false;
        }

        if (passWordEd.getText().toString().equalsIgnoreCase("")) {
            passWordEd.requestFocus();
            T.showShort(this, getString(R.string.pw_hint));
            return false;
        }
        return true;
    }

    @Override
    public void loginSuc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startIntent(NewMainActivity.class);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void saveAccount() {
        SPUtils.put(this, LoginActLogic.KEY_ACCOUNT, userNameEd.getText().toString());
    }

}
