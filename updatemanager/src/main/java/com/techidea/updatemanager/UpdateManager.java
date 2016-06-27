package com.techidea.updatemanager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by zchao on 2016/6/21.
 */
public class UpdateManager {

    private UpdateInfo mUpdateInfo;
    private Context mContext;
    private UpdateOptions mUpdateOptions;
    private HttpRequest mHttpRequest;
    private CheckUpdateTask mCheckUpdateTask;
    private AlertDialog.Builder mUpdateDialogBuilder;
    private AlertDialog mUpdateDialog;
    private DownloadTask mDownloadTask;
    private DownloadTask.Callback mCallback;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private PendingIntent contentIntent = null;
    private int nofityId = 1;

    public UpdateManager(Activity activity) {
        this.mContext = activity;
        mHttpRequest = new HttpRequest();
        mCallback = new DownloadTask.Callback() {
            @Override
            public void onProgressUpdate(int progress) {
                mBuilder.setProgress(100, 50, false);
                mNotificationManager.notify(nofityId, mBuilder.build());
            }

            @Override
            public void onFifish(String filePath) {
                install(filePath);
                mDownloadTask = null;
            }

            @Override
            public void onFailed(String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                mDownloadTask = null;
            }
        };
        contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
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

    private void install(String filePath) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        String[] command = {"chmod", "777", filePath};
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            builder.start();
            installIntent.setDataAndType(Uri.fromFile(new File(filePath)),
                    "application/vnd.android.package-archive");
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(mContext, 0, installIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            Notification noti = mBuilder.build();
            noti.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(0, noti);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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

                    //判断包名是否正确
//                    if (!packageName.equals(updateInfo.getPackageName())) {
//                        return;
//                    }
                    String PREFS_NAME = mContext.getResources().getString(R.string.preference_name);
                    SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String skip_version = sp.getString(mContext.getResources().getString(R.string.preference_key_skip_check_update_version_code), "-1");
                    if (Integer.parseInt(updateInfo.getVersionCode()) > versionCode) {
                        if (!updateInfo.isForceUpdate() &&
                                skip_version.equalsIgnoreCase(updateInfo.getVersionCode())) {
                            // TODO: 2016/6/24  不进行强制更新
                        } else {
                            if (updateInfo.isAutoUpdate()) {
                                showUpdateDialog(updateInfo);
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            mCheckUpdateTask = null;
        }

        @Override
        protected void onCancelled(UpdateInfo updateInfo) {

        }

        @Override
        protected UpdateInfo doInBackground(Void... params) {
            if (!URLUtil.isNetworkUrl(url)) {
                return null;
            }
            try {
                mUpdateInfo = mHttpRequest.post(url, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UpdateInfo updateInfo = new UpdateInfo();
            updateInfo.setAutoUpdate(true);
            updateInfo.setForceUpdate(false);
            updateInfo.setApkUrl("community.apk");
            updateInfo.setPackageName("");
            updateInfo.setUpdateMessage("update");
            updateInfo.setVersionCode("2");
            updateInfo.setVersionName("2.0.1");
            updateInfo.setPackageMD5("");
            mUpdateInfo = updateInfo;
            return mUpdateInfo;
        }
    }

    private void downloadUpdate(final UpdateInfo updateinfo) {
        if (updateinfo == null) {
            return;
        }
        notifyDownload();
        mDownloadTask = new DownloadTask(mContext, mCallback, updateinfo);
        mDownloadTask.execute();
    }

    private void showUpdateDialog(final UpdateInfo updateInfo) {
        mUpdateDialogBuilder = new AlertDialog.Builder(mContext);
        mUpdateDialogBuilder.setTitle("发现新版本");
        if (!TextUtils.isEmpty(updateInfo.getUpdateMessage())) {
            mUpdateDialogBuilder.setMessage(updateInfo.getUpdateMessage());
        }
        mUpdateDialogBuilder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadUpdate(updateInfo);
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialogBuilder.setNeutralButton("跳过", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeSkipVersion(updateInfo);
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialog = mUpdateDialogBuilder.create();
        mUpdateDialog.show();
    }

    private void changeSkipVersion(final UpdateInfo updateInfo) {
        String PREFS_NAME = mContext.getResources().getString(R.string.preference_name);
        SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String skip_version = sp.getString(mContext.getResources().getString(R.string.preference_key_skip_check_update_version_code), "-1");
        if (updateInfo != null) {
            editor.putString(skip_version, updateInfo.getVersionCode());
        }
        editor.commit();
    }

    public void notifyDownload() {
        try {
            if (mNotificationManager == null) {
                mNotificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (mBuilder == null) {
                mBuilder = new NotificationCompat
                        .Builder(mContext);
                mBuilder.setContentTitle("title")
                        .setContentText("download")
                        .setSmallIcon(R.drawable.ali);
            }
            mBuilder.setProgress(100, 0, false);
            mNotificationManager.notify(nofityId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
