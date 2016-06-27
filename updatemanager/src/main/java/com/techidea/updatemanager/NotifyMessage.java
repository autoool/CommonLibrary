package com.techidea.updatemanager;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by zchao on 2016/6/27.
 */
public class NotifyMessage {

    private Context mContext;

    public NotifyMessage(Context context) {
        mContext = context;
    }

    public void notifyMsg(){
        final NotificationManager notificationManager;
        final NotificationCompat.Builder builder;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle("picture download")
                .setContentText("download in progress")
                .setSmallIcon(R.drawable.ali);
        final int id = 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int incr;
                for (incr = 0; incr <= 100; incr += 5) {
                    builder.setProgress(100, incr, false);
                    notificationManager.notify(id, builder.build());
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {

                    }
                }
                builder.setContentText("complete")
                        .setProgress(0, 0, false);
                notificationManager.notify(id, builder.build());
            }
        }).start();
    }
}
