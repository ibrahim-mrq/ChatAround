package com.chat_tracker.Model;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.orhanobut.hawk.Hawk;

import static com.chat_tracker.Activity.MapsActivity.latLng;
import static com.chat_tracker.Activity.MapsActivity.pointName;
import static com.chat_tracker.Activity.Login.SplashScreenActivity.token;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timerx.Action;
import timerx.Stopwatch;
import timerx.StopwatchBuilder;
import timerx.TimeTickListener;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = R.string.app_name + "_Notification_ID";
    Stopwatch stopwatch;
    String Token;

    String PointName;
    LatLng latLngs;

    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(getApplicationContext()).build();
        Token = token;

        if (!Hawk.contains("h" + Token)) {
            Hawk.put("h" + Token, 0L);
        }
        if (!Hawk.contains("s" + Token)) {
            Hawk.put("s" + Token, 0L);
        }
        if (!Hawk.contains("m" + Token)) {
            Hawk.put("m" + Token, 0L);
        }
        long h = Hawk.get("h" + Token);
        long s = Hawk.get("s" + Token);
        long m = Hawk.get("m" + Token);

        if (!Hawk.contains("hh" + Token)) {
            Hawk.put("hh" + Token, 0L);
        }
        if (!Hawk.contains("ss" + Token)) {
            Hawk.put("ss" + Token, 0L);
        }
        if (!Hawk.contains("mm" + Token)) {
            Hawk.put("mm" + Token, 0L);
        }
        long hh = Hawk.get("hh" + Token);
        long mm = Hawk.get("mm" + Token);
        long ss = Hawk.get("ss" + Token);

        long hours = (h + hh) / 3600;
        long minutes = (m + mm) % 60;
        long seconds = (s + ss) % 60;

        if (minutes == 59) {
            Hawk.put("mm" + Token, ((long) 1));
            Hawk.put("hh" + Token, (((long) (hours + 1))));
            Hawk.put("ss" + Token, seconds);
        } else {
            Hawk.put("mm" + Token, minutes);
            Hawk.put("hh" + Token, hours);
            Hawk.put("ss" + Token, seconds);
        }

        Log.d("datedate", hours + ":" + minutes + ":" + seconds);

        crateNotification();
        ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), MyService.class));

        stopwatch = new StopwatchBuilder()
                .startFormat("HH:MM:SS")
                .onTick(new TimeTickListener() {
                    @Override
                    public void onTick(@NonNull CharSequence time) {
                        latLngs = latLng;
                        PointName = pointName;
//                        if (textView != null)
//                            textView.setText(time);
                        long stopwatch_S = stopwatch.getTimeIn(TimeUnit.SECONDS);
                        long stopwatch_H = stopwatch.getTimeIn(TimeUnit.HOURS);
                        long hours = (stopwatch_H) / 3600;
                        long minutes = (stopwatch_S / 60) % 60;
                        long seconds = (stopwatch_S) % 60;
                        Hawk.put("h" + Token, hours);
                        Hawk.put("m" + Token, minutes);
                        Hawk.put("s" + Token, seconds);
                        Hawk.put("location" + Token, latLng);
                        Hawk.put("pointName" + Token, pointName);
                    }
                }).actionWhen(10, TimeUnit.SECONDS, new Action() {
                    @Override
                    public void run() {
                        pushData((((String) new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).format(new Date())))
                                , ((long) Hawk.get("hh" + Token)) + ""
                                , ((long) Hawk.get("mm" + Token)) + ""
                                , ((long) Hawk.get("ss" + Token)) + ""
                        );
                    }
                })
                .build();
        stopwatch.start();


    }

    private void pushData(String date, String h, String m, String s) {
        GeoPoint geoPoint = new GeoPoint(latLngs.latitude, latLngs.longitude);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("h", h);
        map.put("m", m);
        map.put("s", s);
        map.put("latLng", geoPoint);
        map.put("pointName", PointName);
        map.put("id", "id");
        db.collection("User").document(MapsActivity.token).collection("Points")
                .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
//                Hawk.deleteAll();
                Toast.makeText(MyApplication.this, "Complete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
