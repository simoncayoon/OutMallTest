package com.beetron.outmall.models;


/**
 * Created by ShelWee on 14-5-8.
 */
public class UpdateInfo {
    private String appName;
    private String packageName;
    private String appVersion;
    private String appVersionName;
    private String appUrl;
    private String changeLog;
    private String updateTips;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppVersion(String appVersion) {

        this.appVersion = appVersion;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getUpdateTips() {
        return updateTips;
    }

    public void setUpdateTips(String updateTips) {
        this.updateTips = updateTips;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
}
