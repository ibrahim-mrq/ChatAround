package com.chat_tracker.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.chat_tracker.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        if (sp.contains(token))
            token = sp.getString("token", "token");
        else token = null;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}