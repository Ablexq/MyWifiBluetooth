package com.xq.mywifibluetooth.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xq.mywifibluetooth.R;
import com.xq.mywifibluetooth.wifi.WifiActivity;

public class NotificationUtil {

    public static final int NOTIFIED_1 = 1;
    public static final int NOTIFIED_2 = 2;
    public static final int NOTIFIED_3 = 3;
    public static final int NOTIFIED_4 = 4;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification(Context context, int notifyId, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, WifiActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(message)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .build();
        manager.notify(notifyId, notification);
    }

}
