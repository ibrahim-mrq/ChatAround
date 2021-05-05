package com.chat_tracker.Activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chat_tracker.Adapter.ChatAdapter;
import com.chat_tracker.Model.Chat;
import com.chat_tracker.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private RecyclerView mRvChat;
    private EditText mMainTvContent;
    private ImageView mBtnSend;
    private SharedPreferences sharedPreferences;
    private String tokenOne;
    private String tokenTwo;
    private String name;
    private String userSender;
    private String type;
    private String messages;
    private FirebaseFirestore db;
    private ArrayList<Chat> data = new ArrayList<>();
    private CollectionReference ref;
    private CollectionReference ref2;
    private ChatAdapter adapter;
    private String url = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        db.setFirestoreSettings(settings);

        findView();
        sharedPreferences = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        tokenOne = sharedPreferences.getString("token", "");
        Intent intent = getIntent();
        tokenTwo = intent.getStringExtra("TokenTwo");
        name = intent.getStringExtra("name");
        userSender = intent.getStringExtra("userSender");
        type = intent.getStringExtra("type");
        messages = intent.getStringExtra("messages");
        if (messages.equals("privateMessages")) {
            ref = db.collection("message");
            if (!tokenTwo.isEmpty()) {
//                FirebaseMessaging.getInstance().subscribeToTopic(tokenOne);
                getPrivateData();
            }
        }
        if (messages.equals("directChat")) {
            ref2 = db.collection("User").document(tokenTwo).collection("ChatRoom");
        }
        FirebaseMessaging.getInstance().subscribeToTopic(tokenOne);
        getSupportActionBar().setTitle(name + "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onClick();

        adapter = new ChatAdapter(data, ChatActivity.this, tokenOne);
        mRvChat.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        mRvChat.setLayoutManager(manager);
        mRvChat.setItemViewCacheSize(30);
        mRvChat.setDrawingCacheEnabled(true);
        mRvChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRvChat.setItemAnimator(new DefaultItemAnimator());
        mRvChat.setHasFixedSize(true);

        if (messages.equals("privateMessages")) {
            ref.whereEqualTo("token1", tokenOne).orderBy("send_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    adapter.notifyDataSetChanged();
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                        data.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            Chat chat = snapshots.toObject(Chat.class);
                            data.add(chat);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            getPrivateData();
        }
        if (messages.equals("directChat")) {
            ref2.orderBy("send_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    adapter.notifyDataSetChanged();
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                        data.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                            Chat chat = snapshots.toObject(Chat.class);
                            data.add(chat);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            getDirectData();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMainTvContent.getText().toString().isEmpty()) {
                    String content = mMainTvContent.getText().toString();
                    Chat chat = new Chat(tokenOne, tokenTwo, content, userSender, new Date().getTime());
                    mMainTvContent.setText("");
                    if (messages.equals("privateMessages"))
                        ref.add(chat);
                    if (messages.equals("directChat"))
                        ref2.add(chat);
                    sendNotifications(userSender, content, tokenTwo);
                }
            }
        });
    }

    private void findView() {
        mRvChat = findViewById(R.id.rv_chat);
        mMainTvContent = findViewById(R.id.main_tv_content);
        mBtnSend = findViewById(R.id.btn_send);
    }

    private void getPrivateData() {
        ref.orderBy("send_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                adapter.notifyDataSetChanged();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    data.clear();
                    adapter.notifyDataSetChanged();
                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                        Chat chat = snapshots.toObject(Chat.class);
                        if (chat.getToken1().equals(tokenOne) && chat.getToken2().equals(tokenTwo)
                                || chat.getToken1().equals(tokenTwo) && chat.getToken2().equals(tokenOne)
                        )
                            data.add(chat);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void getDirectData() {
        ref2.orderBy("send_date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                adapter.notifyDataSetChanged();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    data.clear();
                    adapter.notifyDataSetChanged();
                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
                        Chat chat = snapshots.toObject(Chat.class);
//                        if (chat.getToken1().equals(tokenOne) && chat.getToken2().equals(tokenTwo)
//                                || chat.getToken1().equals(tokenTwo) && chat.getToken2().equals(tokenOne)
//                        )
                        data.add(chat);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendNotifications(String title, String body, String topic) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/" + topic);
            JSONObject object = new JSONObject();
            object.put("title", title + "");
            object.put("body", body + "");
            jsonObject.put("notification", object);
//            jsonObject.put("data", object);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Error", response + "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", error.getMessage() + "");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "key=AAAAmDBGLmc:APA91bHE_8Cb-QfPlYmzqPgyacUAab0CSiO34IO65VVacfYq4FcJFeEd5pqCUcXgjkO4owNvi5Pfl5z7w1hAGpm78crhzBtBq3A6JjF965YxSrl2UjFdWFnymLiXrXpKnbrF7CvYH6MN");
                    return header;
                }
            };
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}