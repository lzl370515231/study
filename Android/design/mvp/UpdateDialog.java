package com.van.eba.dialog.Update;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;

import com.van.eba.R;
import com.van.eba.dialog.UpdateLoading.UpdateLoadingDialog;
import com.van.eba.http.xutils.HttpCallBack;
import com.van.eba.http.xutils.HttpManager;
import com.van.eba.model.VanException;
import com.van.eba.model.VersionInfo;
import com.van.eba.model.request.versionCheck;
import com.van.eba.model.response.VersionCheckResponse;
import com.van.eba.tools.DialogUtil;
import com.van.eba.tools.L;
import com.van.eba.tools.SPUtils;
import com.van.eba.tools.Utils;
import com.van.eba.ui.App;
import com.van.eba.widget.Dialog2Btn;

/**
 * 版本检测更新事件
 * Created by zhangliao on 2016/11/7.
 */
public class UpdateDialog {

    public IUpdateCheckAction iUpdateCheckAction;

    private FragmentActivity context;
    private String versionname = "";

    public UpdateDialog(FragmentActivity context, IUpdateCheckAction iUpdateCheckAction) {
        this.context = context;
        this.iUpdateCheckAction = iUpdateCheckAction;
    }

    public void checkUpdateAction(final boolean isShowUpdateDialog, boolean isShowProgress) {
        checkUpdateAction(isShowUpdateDialog, true, isShowProgress);
    }


    /**
     * * 版本检测
     *
     * @param isShowUpdateDialog 是否弹出更新提示框
     * @param isShowFailDialog   是否显示失败对话框
     * @param isShowProgress     是否显示网络请求加载条
     */
    public void checkUpdateAction(final boolean isShowUpdateDialog, final boolean isShowFailDialog, boolean isShowProgress) {
        String localMD5 = Utils.getSignMd5Str(context);
        L.i("版本MD5值---> " + localMD5);

        versionCheck check = new versionCheck();
        check.setClientType(1);
        HttpManager.getInstance(App.getApp(context)).post(check, new HttpCallBack<VersionCheckResponse>(context, "版本检测", isShowProgress, isShowProgress) {
            @Override
            public void onSuccess(VersionCheckResponse responseInfo) {
                if (responseInfo.isSuccess()) {
                    if (responseInfo.getData() != null && responseInfo.getData() != null) {
                        //成功
                        VersionInfo info = responseInfo.getData();
                        versionname = info.getName();
                        checkIsUpdate(info, isShowUpdateDialog, isShowFailDialog);
                    } else {
                        if (isShowFailDialog) {
                            showMsgDialog(context.getString(R.string.data_error));
                        }
                    }
                } else {
                    if (isShowFailDialog) {
                        showMsgDialog(responseInfo.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(VanException error) {
            }

            @Override
            public void cancelAction(VanException error) {
            }
        });
    }

    /**
     * 检测是否需要升级
     *
     * @param info
     */
    private void checkIsUpdate(VersionInfo info, boolean showDialog, boolean isShowFailDialog) {
        String versionCode = Utils.getAppVersionName(context);

        //测试
//        String url = "https://dl.wandoujia.com/files/jupiter/latest/wandoujia-web_seo_baidu_homepage.apk";
//        info.setAppUrl(url);

        /**
         * 0 则相等，
         * 负值：前字符串的值小于后字符串， <
         * 正值：前字符串大于后字符串 >
         */
        //如果服务端的版本不等于当前的版本，则提示升级
        if (info.getName().compareTo(versionCode) != 0) {

            //强制升级
            if (info.getForce() == 1) {
                    showUpdateDialog(info, true, info.getName().compareTo(versionCode) > 0 ? true:false);
            } else {
                //如果不是强制升级，则比对上次取消的版本，如果服务器版本和上次取消的一致则不弹框
                String name = SPUtils.get(context, SPUtils.CANCEL_UPDATE_VERSIONNAME, "").toString();

                if (isShowFailDialog) {//点击检测更新的按钮进行的版本检测
                    showUpdateDialog(info, false, info.getName().compareTo(versionCode) > 0 ? true:false);
                } else { //启动时默认进行的版本检测
                    if (!name.equals(versionname))
                        showUpdateDialog(info, false, info.getName().compareTo(versionCode) > 0 ? true:false);
                }
            }

            //显示小红点
            if (iUpdateCheckAction != null) {
                iUpdateCheckAction.setBadgeNumber();
            }

        } else {
            if (isShowFailDialog) {
                showMsgDialog("已经是最新版本!");
            }
        }
    }

    /**
     * 显示更新对话框
     * isUpdate  更新还是回退版本
     */
    public void showUpdateDialog(final VersionInfo info, boolean isEnforceUpdate, boolean isUpdate) {

        String title = "版本更新";
        if (!isUpdate)
            title = "版本回退";
        String updateText = "最新版本 " + info.getName()
                + "\n\n更新内容：\n"
                + info.getUpdateDesc();
        if (!isUpdate)
            updateText = "回退版本至 " + info.getName()
                    + "\n\n版本介绍：\n"
                    + info.getUpdateDesc();

        //强制更新
        if (isEnforceUpdate) {
            showDialog(title, updateText, context.getString(R.string.return_desk), context.getString(R.string.update_now),
                    new Dialog2Btn.Dialog2BtnLintener() {
                        @Override
                        public void leftAction(DialogFragment dialogFragment) {
                            dialogFragment.dismiss();
                            if (iUpdateCheckAction != null) {
                                iUpdateCheckAction.enforceExit();
                            }
                        }

                        @Override
                        public void rightAction(DialogFragment dialogFragment) {
                            dialogFragment.dismiss();
                            showLoading(info.getApk_url(), info.getMdCode());
                        }
                    }, false);
        } else {
            showDialog(title, updateText, context.getString(R.string.update_later), context.getString(R.string.update_now),
                    new Dialog2Btn.Dialog2BtnLintener() {
                        @Override
                        public void leftAction(DialogFragment dialogFragment) {
                            dialogFragment.dismiss();
                            //把当前时间存下来
                            SPUtils.put(context, SPUtils.CANCEL_UPDATE_VERSIONNAME, versionname);

                            if (iUpdateCheckAction != null) {
                                iUpdateCheckAction.cancelAction();
                            }
                        }

                        @Override
                        public void rightAction(DialogFragment dialogFragment) {
                            dialogFragment.dismiss();
                            showLoading(info.getApk_url(), info.getMdCode());
                        }
                    }, true);
        }
    }

    /**
     * 升级提示框
     */
    private Dialog2Btn dialog2Btn;

    /**
     * 弹出升级提示框
     *
     * @param title
     * @param msg
     * @param cancelMsg
     * @param okMsg
     * @param lintener
     * @param isCanback
     */
    private void showDialog(String title, String msg,
                            String cancelMsg, String okMsg, Dialog2Btn.Dialog2BtnLintener lintener, boolean isCanback) {
        if (dialog2Btn != null) {
            dialog2Btn.dismiss();
            dialog2Btn = null;
        }

        dialog2Btn = new Dialog2Btn();
        dialog2Btn.setTitle(title);
        dialog2Btn.setTitleGravity(Gravity.CENTER);
        dialog2Btn.setMsg(msg);
        dialog2Btn.setMsgGravity(Gravity.LEFT);
        dialog2Btn.setBtnTag(cancelMsg, okMsg);
        dialog2Btn.setLintener(lintener);
        dialog2Btn.setCanback(isCanback);
        dialog2Btn.setCancel(isCanback);
        dialog2Btn.show(context.getSupportFragmentManager(), "");
    }

    /**
     * 显示下载进度条
     */
    public void showLoading(String downUrl, String md5Code) {
        UpdateLoadingDialog loadingDialog = new UpdateLoadingDialog();
        loadingDialog.setMd5Code(md5Code);
        loadingDialog.setDownUrl(downUrl);
        loadingDialog.show(context.getSupportFragmentManager(), "");
    }

    private void showMsgDialog(String msg) {
        DialogUtil.showDialog(context, msg);
    }

}
