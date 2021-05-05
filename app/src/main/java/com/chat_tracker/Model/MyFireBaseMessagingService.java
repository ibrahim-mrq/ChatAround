package com.chat_tracker.Model;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.chat_tracker.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        sendMyNotification(title, body);


    }

    @Override
    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
    }


    private void sendMyNotification(String title, String body) {

//        Intent intent = new Intent(this, Chat.class);
//        intent.putExtra("title", title);
//        intent.putExtra("body", body);
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "TAC")
//                new NotificationCompat.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_chat)
                        .setContentTitle(title)
//                        .addAction(R.drawable.ic_build, "send", pendingIntent)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(body);
        int id = (int) System.currentTimeMillis();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TAC", "Ibrahim Mrq", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(id, builder.build());
    }
}
