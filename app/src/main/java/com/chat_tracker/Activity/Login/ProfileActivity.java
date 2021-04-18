package com.chat_tracker.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat_tracker.Activity.ChatActivity;
import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.Adapter.TabAdapter;
import com.chat_tracker.Fragment.GalleryFragment;
import com.chat_tracker.Fragment.ProfileFragment;
import com.chat_tracker.Model.TabClass;
import com.chat_tracker.R;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;
    private String name, img, token;
    private TextView textView;
    private CircleImageView imageView;
    private ImageView imageDirect, imageFriend;
    private ArrayList<String> listId;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        spEditor = sp.edit();

        name = getIntent().getStringExtra("name");
        img = getIntent().getStringExtra("img");
        token = getIntent().getStringExtra("token");
        listId = getIntent().getStringArrayListExtra("listId");


        textView = findViewById(R.id.profile_tv_name);
        imageView = findViewById(R.id.profile_img);
        imageDirect = findViewById(R.id.profile_imageDirect);
        imageFriend = findViewById(R.id.profile_imageFriend);
        if (!img.isEmpty())
            Picasso.get().load(img).into(imageView);
        if (!sp.getString("token", "token").equals(token)) {
            imageDirect.setVisibility(View.GONE);
            imageFriend.setVisibility(View.GONE);
        } else {
            imageDirect.setVisibility(View.VISIBLE);
            imageFriend.setVisibility(View.VISIBLE);
        }

        tabLayout = findViewById(R.id.tab);
        pager = findViewById(R.id.v_pager);

        textView.setText(name);

        ArrayList<TabClass> tabs = new ArrayList<>();
        tabs.add(new TabClass("الملف الشخصي", new ProfileFragment()));
        tabs.add(new TabClass("معرض الصور", new GalleryFragment()));

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), tabs);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        imageDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putExtra("TokenTwo", token);
                intent.putExtra("name", name);
                intent.putExtra("userSender", name);
                intent.putExtra("type", "User");
                intent.putExtra("messages", "directChat");
                startActivity(intent);
            }
        });
        imageFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("listId", listId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}