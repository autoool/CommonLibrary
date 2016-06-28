package com.techidea.updatemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zchao on 2016/6/27.
 */
public class DownloadTask extends AsyncTask<String, Integer, Boolean> {

    private static final int BUFFER_SIZE = 10 * 1024;

    private Context mContext;
    private UpdateInfo mUpdateInfo;
    private InputStream in = null;
    private FileOutputStream out = null;
    private String filePath = "";
    private String errorMsg = "";


    public interface Callback {
        void onProgressUpdate(int progress);

        void onSuccess(String filepath);

        void onFailed();
    }

    private Callback mCallback;

    public DownloadTask(Context context, Callback callback, UpdateInfo updateInfo) {
        this.mContext = context;
        this.mUpdateInfo = updateInfo;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            URL url = new URL(mUpdateInfo.getApkUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setReadTimeout(5 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            if (urlConnection.getResponseCode() == 200) {
                in = urlConnection.getInputStream();
                File dir = mContext.getApplicationContext().getExternalCacheDir();
                String appName = mUpdateInfo.getApkUrl().substring(
                        mUpdateInfo.getApkUrl().lastIndexOf("/") + 1, mUpdateInfo.getApkUrl().length()
                );
                File apkFile = new File(dir, appName);
                out = new FileOutputStream(apkFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int oldProgress = 0;
                while ((byteread = in.read(buffer)) != -1) {
                    bytesum += byteread;
                    out.write(buffer, 0, byteread);
                    int progress = (int) (bytesum * 100L / bytetotal);
                    if (progress != oldProgress) {
                        if (mCallback != null)
                            mCallback.onProgressUpdate(progress);
                    }
                    oldProgress = progress;
                }
                filePath = apkFile.getAbsolutePath();
                return true;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            errorMsg = ioe.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                errorMsg = ioe.getMessage();
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                errorMsg = ioe.getMessage();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!success) {
            handleErrorMessage(errorMsg);
            if (mCallback != null)
                mCallback.onFailed();
        } else {
            if (!TextUtils.isEmpty(filePath))
                if (mCallback != null)
                    mCallback.onSuccess(filePath);
        }
    }

    public void handleErrorMessage(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("下载失败");
        if (!TextUtils.isEmpty(msg) && msg.length() > 200) {
            msg.substring(0, 150);
        }
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
