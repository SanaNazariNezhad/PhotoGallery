package org.maktab.photogallery.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.maktab.photogallery.PhotoGalleryApplication;
import org.maktab.photogallery.event.NotificationEvent;

import io.reactivex.functions.Consumer;

public class VisibleFragment extends Fragment {

    private Context mContext = getContext();

    @Override
    public void onStart() {
        super.onStart();


//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

//        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("CheckResult")
    private void subcribe() {
        PhotoGalleryApplication photoGalleryApplication= new PhotoGalleryApplication();
        photoGalleryApplication.bus()
                .toObservable()
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (object instanceof NotificationEvent) {
                            String msg = "The fragment received the notification event";
                            Log.d(PhotoGalleryApplication.TAG_EVENT_BUS, msg);
                        }
                    }
                });
    }

    /*@Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
    public void onNotificationEventListener(NotificationEvent notificationEvent) {
        String msg = "The fragment received the notification event";
        Log.d(PhotoGalleryApplication.TAG_EVENT_BUS, msg);

        EventBus.getDefault().cancelEventDelivery(notificationEvent);
    }*/
}
