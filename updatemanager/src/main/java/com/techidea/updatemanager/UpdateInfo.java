package com.techidea.updatemanager;

/**
 * Created by zchao on 2016/6/21.
 */
public class UpdateInfo {

    private String updateMessage;
    private String apkUrl;
    private String versionCode;
    private String versionName;
    private String packageName;
    private String packageSize;
    private String packageMD5;
    private boolean forceUpdate;
    private boolean autoUpdate;

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public String getPackageMD5() {
        return packageMD5;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }
}
