package com.techidea.updatemanager;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zchao on 2016/6/27.
 */
public class DownloadTask extends AsyncTask<String, Integer, Void> {

    private static final int BUFFER_SIZE = 10 * 1024;

    private Context mContext;
    private UpdateInfo mUpdateInfo;
    private InputStream in = null;
    private FileOutputStream out = null;

    public interface Callback {
        void onProgressUpdate(int progress);

        void onFifish(String filepath);

        void onFailed(String msg);

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
    protected Void doInBackground(String... params) {
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
                if (mCallback != null)
                    mCallback.onFifish(apkFile.getAbsolutePath());
            } else {
                // TODO: 2016/6/27 下载出错
                if (mCallback != null)
                    mCallback.onFailed(urlConnection.getResponseMessage());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            if (mCallback != null)
                mCallback.onFailed(ioe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if (mCallback != null)
                mCallback.onFailed(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


}
