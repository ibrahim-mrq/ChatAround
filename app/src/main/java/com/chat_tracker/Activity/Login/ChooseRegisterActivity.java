package com.chat_tracker.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chat_tracker.R;

public class ChooseRegisterActivity extends AppCompatActivity {

    Button btnUser, btnPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_register);

        btnUser = findViewById(R.id.chooser_btn_user);
        btnPlace = findViewById(R.id.chooser_btn_place);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterActivity.this, RegisterActivity.class));
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterActivity.this, RegisterPlaceActivity.class));
            }
        });
    }
}