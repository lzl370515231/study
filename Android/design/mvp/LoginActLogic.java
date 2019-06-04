package com.van.eba.ui.login;

import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;

import com.csipsdk.sdk.pjsua2.Logger;
import com.van.eba.R;
import com.van.eba.constant.Constants;
import com.van.eba.db.dao.AppSourceDao;
import com.van.eba.db.dao.Config;
import com.van.eba.db.dao.ConfigDao;
import com.van.eba.http.ThreadPool;
import com.van.eba.http.xutils.HttpCallBack;
import com.van.eba.http.xutils.HttpManager;
import com.van.eba.model.MenuSource;
import com.van.eba.model.OrderAuth;
import com.van.eba.model.UserInfo;
import com.van.eba.model.VanException;
import com.van.eba.model.request.login;
import com.van.eba.model.response.LoginRespose;
import com.van.eba.tools.EncryptTools;
import com.van.eba.tools.SPUtils;
import com.van.eba.ui.App;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 登陆 --- 逻辑层
 * 作者：zhangliao
 * 时间：2016/9/6 16:02
 */
public class LoginActLogic {

    public static final String KEY_ACCOUNT = "account";

    private Context context;

    private App app;

    private ILoginAct iLoginAct;

    public LoginActLogic(Context context, ILoginAct iLoginAct) {
        this.context = context;
        this.app = App.getApp(context);
        this.iLoginAct = iLoginAct;
    }

    /**
     * 登录方法
     *
     * @param login
     */
    public void loginAction(login login) {
        // 极光推送id
        String rid = JPushInterface.getRegistrationID(app);
        login.setRegistId(rid);

        //密码加密
        String eCodeStr = EncryptTools.eCode(login.getPassword());
//        login.setPassword(eCodeStr);
        Logger.error("LZL",""+login.toString());
        HttpManager.getInstance(app).post(login, new HttpCallBack<LoginRespose>(context, "登陆") {

            @Override
            public void onSuccess(LoginRespose responseInfo) {
                praseStats(responseInfo);
            }

            @Override
            public void onFailure(VanException error) {
                iLoginAct.showMsgDialog(error.getMsg());
            }

            @Override
            public void cancelAction(VanException error) {

            }
        });
    }

    /**
     * 登陆成功后，数据解析
     *
     * @param respose
     */
    private void praseStats(final LoginRespose respose) {
        if (respose.isSuccess()) {
            if (respose.getData() != null) {
                ThreadPool.getIntance().executor(new Runnable() {
                    @Override
                    public void run() {

                        //设备房组态那边有个需要缓存的要判断下权限


                        //把用户基本信息保存至sqlite，先要删除配置表里面的数据
                        ConfigDao.getInstance(app).delAll();
                        Config config = new Config();
                        UserInfo info = respose.getData().getUser();
                        if (info != null) {
                            //保存账号
                            iLoginAct.saveAccount();

                            config.setName(info.getName());
                            if (TextUtils.isEmpty(info.getName()))
                                config.setName(info.getUserName());
                            config.setAccount(info.getAccount());
                            config.setUserName(info.getUserName());
                            config.setEmployeeNo(info.getEmployeeNo());
                            config.setTenantId(info.getTenantId());
                            config.setuId(info.getUid());
                            config.setToken(respose.getData().getToken());
                            config.setRoleName(info.getrName());
                            config.setOrgLevel(info.getOrgLevel());
                            config.setOrgShowStr(info.getOrgShowStr());
                            config.setMobile(info.getMobilePhone());
                        } else {
                            iLoginAct.showMsgDialog(context.getString(R.string.login_fails));
                            return;
                        }

                        OrderAuth orderAuth = respose.getData().getOrderAuth();
                        if (null != orderAuth) {
                            config.setTransferOrders(orderAuth.getTransferOrders());
                            config.setCheckAndAccept(orderAuth.getCheckAndAccept());
                            config.setShow(orderAuth.getShow());
                            config.setTakeWorkOrder(orderAuth.getTakeWorkOrder());
                            config.setClose(orderAuth.getClose());
                            config.setSendOrders(orderAuth.getSendOrders());
                        }
                        ConfigDao.getInstance(app).save(config);

//                        ArrayList<String> titles = new ArrayList<>();


                        //把权限保存到数据库
                        AppSourceDao.getInstance(app).delAll(); //先删除
                        AppSourceDao.getInstance(app).delMenu();
                        AppSourceDao.getInstance(app).dropTable();
                        AppSourceDao.getInstance(app).dropMenyTable();
                        List<MenuSource> menuSources = respose.getData().getMenu();
                        AppSourceDao.getInstance(app).saveMenuList(menuSources);
                        if (menuSources != null && menuSources.size() != 0) {
                            for (MenuSource menu : menuSources) {
//                                titles.add(menu.getLabel());
                                if (menu.getChildren() != null) {
                                    AppSourceDao.getInstance(app).saveList(menu.getChildren());
                                }
                            }
                        }

//                        titles.clear();
//                        titles.add("EBA");
//                        titles.add("停车");
//                        titles.add("人员定位");
//                        titles.add("其他");
//                        //讲菜单组别数据保存在本地
//                        SPUtils.put(context, Constants.MENU_TITLE,titles);

                        SPUtils.put(context, "selectRoomData", "");//查看设备房组态数据时，要记住第一次的选择，切换用户就进行初始化
                        SPUtils.put(context, "CollectionDeviceMainAct_roomId", -10086);//设备数据修正时，要记住第一次的选择，切换用户就进行初始化

                        iLoginAct.loginSuc();
                    }
                });
            } else {
                iLoginAct.showMsgDialog(context.getString(R.string.data_error));
            }
        } else {
            iLoginAct.showMsgDialog(respose.getMsg());
        }
    }
}
