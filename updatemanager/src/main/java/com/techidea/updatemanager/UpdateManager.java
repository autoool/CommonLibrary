package com.techidea.updatemanager;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

/**
 * Created by zchao on 2016/6/21.
 */
public class UpdateManager {

    private UpdateInfo mUpdateInfo;
    private Context mContext;
    private UpdateOptions mUpdateOptions;
    private HttpRequest mHttpRequest;
    private CheckUpdateTask mCheckUpdateTask;

    private static final String savePath = "/sdcard/annel_yunpos";
    private static final String saceFileName = savePath + "";
    private ProgressBar mProgressBar;

    private Thread dowoadThread;
    private DownloadManager mDownloadManager;

    public UpdateManager(Context context) {
        this.mContext = context;
        mHttpRequest = new HttpRequest();
    }

    public UpdateManager(Context context, UpdateOptions options) {
        check(context, options);
        mHttpRequest = new HttpRequest();
    }


    public void check(Context context, UpdateOptions options) {
        if (context != null)
            this.mContext = context;
        if (context == null)
            return;
        if (options == null)
            return;
        else
            this.mUpdateOptions = options;
        mCheckUpdateTask = new CheckUpdateTask
                (options.getCheckUrl(), options.getParams());
        mCheckUpdateTask.execute();
    }

    private void install(Context context, String filePath) {

    }

    private class CheckUpdateTask extends AsyncTask<Void, Integer, UpdateInfo> {
        private String url;
        private String param;

        public CheckUpdateTask(String url, String param) {
            this.url = url;
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(UpdateInfo updateInfo) {
            if (mContext != null && updateInfo != null) {
                try {
                    PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                    Integer versionCode = packageInfo.versionCode;
                    String versionName = packageInfo.versionName;
                    String packageName = mContext.getPackageName();

                    if (!packageName.equals(updateInfo.getPackageName())) {
                        return;
                    }

                    String PREFS_NAME = mContext.getResources().getString(R.string.preference_name);
                    SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                    String skip_version = sp.getString(mContext.getResources().getString(R.string.preference_key_skip_check_update_version_code),"-1");
                    if (Integer.parseInt(updateInfo.getVersionCode())>versionCode){
                        if (!updateInfo.isForceUpdate()&&
                                !skip_version.equalsIgnoreCase(updateInfo.getVersionCode())){
                            //// TODO: 2016/6/24  不进行强制更新
                        }else{
                            if (updateInfo.isAutoUpdate()){
                                // TODO: 2016/6/24 自动更新，显示自动更新对话框
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {

                }

            }
        }

        @Override
        protected void onCancelled(UpdateInfo updateInfo) {

        }

        @Override
        protected UpdateInfo doInBackground(Void... params) {
            if (!URLUtil.isNetworkUrl(url)) {
                return null;
            }
            mUpdateInfo = mHttpRequest.post(url, param);
            return mUpdateInfo;
        }
    }


}
