package org.maktab.photogallery;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.maktab.photogallery.event.NotificationEvent;
import org.maktab.photogallery.event.RxBus;

import io.reactivex.functions.Consumer;

public class PhotoGalleryApplication extends Application {

    private static final String TAG = "PhotoGalleryApplication";
    public static final String TAG_EVENT_BUS = "PGEventBus";
    private RxBus bus;
    private Context mContext = this;

    /*@Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
        createNotificationChannel();

        EventBus.getDefault().register(this);
    }*/

    @Override
    public void onTerminate() {
        super.onTerminate();

//        EventBus.getDefault().unregister(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channel_id);
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /*@Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void onNotificationEventListener(NotificationEvent notificationEvent) {
        String msg = "Application received the notification event";
        Log.d(TAG_EVENT_BUS, msg);

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(
                notificationEvent.getNotificationId(),
                notificationEvent.getNotification());
    }*/

    @SuppressLint("CheckResult")
    private void subcribe() {
                bus()
                .toObservable()
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (object instanceof NotificationEvent) {
                            NotificationEvent notificationEvent = (NotificationEvent) object;
                            NotificationManagerCompat notificationManagerCompat =
                                    NotificationManagerCompat.from(mContext);
                            notificationManagerCompat.notify(
                                    notificationEvent.getNotificationId(),
                                    notificationEvent.getNotification());
                        }
                    }
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new RxBus();
    }

    public RxBus bus() {
        return bus;
    }
}
