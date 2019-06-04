package com.van.eba.model.request;

/**
 * 登陆请求实体
 * Created by zhangliao on 2016/10/14.
 */
public class login extends RequestBase{

    /**
     * 用户账号
     */
    private String userName;

    private String loginDeviceIMEI;
    private String loginDeviceType;

    /**
     * 密码
     */
    private String password;

    /** 极光推送的id  */
    private String registId;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistId() {
        return registId;
    }

    public void setRegistId(String registId) {
        this.registId = registId;
    }

    @Override
    public String getUrl() {
        return "mbLogin";
    }

    public String getLoginDeviceIMEI() {
        return loginDeviceIMEI;
    }

    public void setLoginDeviceIMEI(String loginDeviceIMEI) {
        this.loginDeviceIMEI = loginDeviceIMEI;
    }

    public String getLoginDeviceType() {
        return loginDeviceType;
    }

    public void setLoginDeviceType(String loginDeviceType) {
        this.loginDeviceType = loginDeviceType;
    }

    @Override
    public String toString() {
        return "login{" +
                "userName='" + userName + '\'' +
                ", loginDeviceIMEI='" + loginDeviceIMEI + '\'' +
                ", loginDeviceType='" + loginDeviceType + '\'' +
                ", password='" + password + '\'' +
                ", registId='" + registId + '\'' +
                '}';
    }
}
