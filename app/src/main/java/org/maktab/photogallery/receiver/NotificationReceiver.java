package org.maktab.photogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import org.maktab.photogallery.utils.ServicesUtils;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String TAG = "PGNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent: " + intent);
        if (getResultCode() != Activity.RESULT_OK) {
            Log.d(TAG, "The notification request has been canceled");
            return;
        }

        int notificationId = intent.getIntExtra(ServicesUtils.EXTRA_NOTIFICATION_ID, 0);
        Notification notification = intent.getParcelableExtra(ServicesUtils.EXTRA_NOTIFICATION);

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification);
    }
}