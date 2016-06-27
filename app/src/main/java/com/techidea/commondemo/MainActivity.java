package com.techidea.commondemo;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.techidea.commondemo.menu.ArcMenuActivity;
import com.techidea.commondemo.adapter.GridViewAdapter;
import com.techidea.commondemo.adapter.PayItem;
import com.techidea.commondemo.menu.RayMenuActivity;
import com.techidea.commondemo.recyclerview.GridActivity;
import com.techidea.updatemanager.DownloadTask;
import com.techidea.updatemanager.HttpRequest;
import com.techidea.updatemanager.NotifyMessage;
import com.techidea.updatemanager.UpdateInfo;
import com.techidea.updatemanager.UpdateManager;
import com.techidea.updatemanager.UpdateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.gridview)
    GridView mGridView;

    private List<PayItem> mPayItemList;
    private GridViewAdapter mGridViewAdapter;
    private UpdateManager mUpdateManager;
    private UpdateOptions mUpdateOptions;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        mGridViewAdapter = new GridViewAdapter(this, mPayItemList, R.layout.view_pay_item);
        mGridView.setAdapter(mGridViewAdapter);
        Map<String, String> params = new HashMap<>();
        mUpdateOptions = new UpdateOptions.Builder(this)
                .checkUrl("https://github.com/chaozaiai/article/blob/master/app-alpha_commontest-release-unsigned.apk")
                .checkPackageName(false)
                .setParam(params)
                .build();
        mUpdateManager = new UpdateManager(this);
        mUpdateManager.check(this, mUpdateOptions);


        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("picture download")
                .setContentText("download in progress")
                .setSmallIcon(R.drawable.ic_eat);

    }

    private void initData() {
        mPayItemList = new ArrayList<>();
        mPayItemList.add(new PayItem(R.drawable.ali, "支付宝"));
        mPayItemList.add(new PayItem(R.drawable.weixin, "微信"));
        mPayItemList.add(new PayItem(R.drawable.card, "银行卡"));
        mPayItemList.add(new PayItem(R.drawable.cash, "现金"));
        mPayItemList.add(new PayItem(R.drawable.discount, "折扣"));
    }

    @OnClick(R.id.button_recycler_list)
    void buttonList() {
        try {
            UpdateInfo updateInfo = new UpdateInfo();
            updateInfo.setAutoUpdate(true);
            updateInfo.setForceUpdate(false);
            updateInfo.setApkUrl("community.apk");
            updateInfo.setPackageName("");
            updateInfo.setUpdateMessage("update");
            updateInfo.setVersionCode("2");
            updateInfo.setVersionName("2.0.1");
            updateInfo.setPackageMD5("");
            DownloadTask downloadTask = new DownloadTask(this, new DownloadTask.Callback() {
                @Override
                public void onProgressUpdate(int progress) {

                }

                @Override
                public void onFifish(String filepath) {

                }

                @Override
                public void onFailed(String msg) {

                }
            }, updateInfo);
            downloadTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button_recycler_grid)
    void buttonGrid() {
        startActivity(new Intent(this, GridActivity.class));
    }

    @OnClick(R.id.button_arcmenu)
    void buttonArcMenu() {
        startActivity(new Intent(this, ArcMenuActivity.class));
    }

    @OnClick(R.id.button_raymenu)
    void buttonRayMenu() {
        startActivity(new Intent(this, RayMenuActivity.class));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
