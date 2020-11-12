package org.maktab.photogallery.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.maktab.photogallery.R;
import org.maktab.photogallery.data.model.GalleryItem;
import org.maktab.photogallery.data.repository.PhotoRepository;
import org.maktab.photogallery.utils.QueryPreferences;
import org.maktab.photogallery.view.activity.PhotoGalleryActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollJobService extends JobService {

    private static final String TAG = "PollJobService";
    private static final int JOB_ID = 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollJobService.class);
    }

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, PollJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, component)
                // schedule it to run any time between 1 - 5 minutes
                .setMinimumLatency(60)
                .setOverrideDeadline(5 * 60);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onHandleIntent: " + PollJobService.newIntent(getApplicationContext()));
        if (!isNetworkAvailableAndConnected()) {
            Log.d(TAG, "Network not available");
            return false;
        }

        String query = QueryPreferences.getSearchQuery(this);

        PhotoRepository repository = new PhotoRepository();
        List<GalleryItem> items;
        if (query == null)
            items = repository.fetchPopularItems();
        else
            items = repository.fetchSearchItems(query);

        if (items == null || items.size() == 0) {
            Log.d(TAG, "Items from server not fetched");
            return false;
        }

        String serverId = items.get(0).getId();
        String lastSavedId = QueryPreferences.getLastId(this);
        if (!serverId.equals(lastSavedId)) {
            Log.d(TAG, "show notification");
            createAndShowNotification();
        } else {
            Log.d(TAG, "do nothing");
        }

        QueryPreferences.setLastId(this, serverId);
        return false;
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected())
            return true;

        return false;
    }

    public static void jobSchedule(Context context, boolean isOn) {
        Log.d(TAG, "jobSchedule");
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        PendingIntent operation = getPendingIntent(context, 0);

        ComponentName serviceName = new ComponentName(context.getPackageName(),
                PollJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);

        if (isOn) {
            Log.d(TAG, "schedule On");
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(1));

            JobInfo myJobInfo = builder.build();
            jobScheduler.schedule(myJobInfo);
        } else {
            Log.d(TAG, "schedule Off");
            /*jobScheduler.cancelAll();
            jobScheduler = null;*/
        }
    }

    public static boolean isAlarmSet(Context context) {
        PendingIntent operation = getPendingIntent(context, PendingIntent.FLAG_NO_CREATE);
        return operation != null;
    }

    private static PendingIntent getPendingIntent(Context context, int flags) {
        Intent intent = PollJobService.newIntent(context);
        return PendingIntent.getService(
                context,
                0,
                intent,
                flags);
    }

    private void createAndShowNotification() {
        /*AssetManager assetManager = getAssets();
        Uri notficationSound = Uri.parse("file:///android_asset/notification/got_it_done.mp3");
        try {
            String[] fileNames = assetManager.list(NOTIFICATION);
            for (int i = 0; i <fileNames.length ; i++) {

                String assetPath = NOTIFICATION + File.separator + fileNames[i];
                notficationSound = Uri.parse(assetPath);
            }*/
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                PhotoGalleryActivity.newIntent(this),
                0);

        String channelId = getResources().getString(R.string.channel_id);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getResources().getString(R.string.new_pictures_title))
                .setContentText(getResources().getString(R.string.new_pictures_text))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, notification);

        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
