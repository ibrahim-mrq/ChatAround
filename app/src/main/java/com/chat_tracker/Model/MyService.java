package com.chat_tracker.Model;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.chat_tracker.Activity.MapsActivity.token;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.R;

import static com.chat_tracker.Model.MyApplication.CHANNEL_ID;

import com.orhanobut.hawk.Hawk;

public class MyService extends Service {

    public MyService() {
    }

    long hh;
    long mm;
    long ss;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Hawk.contains("mm" + token)) {
            mm = Hawk.get("mm" + token);
        }
        if (Hawk.contains("hh" + token)) {
            hh = Hawk.get("hh" + token);
        }
        if (Hawk.contains("ss" + token)) {
            ss = Hawk.get("ss" + token);
        }

        Intent intent1 = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(hh + ":" + mm + ":" + ss)
                .setContentTitle("Run last time application")
                .setSmallIcon(R.drawable.ic_chat)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
