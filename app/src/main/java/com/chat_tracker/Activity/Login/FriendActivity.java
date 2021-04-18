package com.chat_tracker.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.Adapter.UserAdapter;
import com.chat_tracker.Interface.UserInterface;
import com.chat_tracker.Model.User;
import com.chat_tracker.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FriendActivity extends AppCompatActivity {

    private RecyclerView rv;
    private UserAdapter adapter;
    private ArrayList<User> list = new ArrayList<>();
    private ArrayList<String> listId = new ArrayList<>();
    private String token;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        db = FirebaseFirestore.getInstance();
        token = getIntent().getStringExtra("token");
        listId = getIntent().getStringArrayListExtra("listId");

        rv = findViewById(R.id.rv_friend);
        getData();
    }

    private void getData() {
        db.collection("User")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                if (listId.contains(user.getToken())) {
                                    Log.d("listId", user.getToken() + "");
                                    list.add(user);
                                }
                            }
//                            for (int i = 0; i < listId.size(); i++) {
//                                if (user.getToken().equals(listId.get(i))) {
//                                    Log.d("listId", listId.get(i) + "");
//                                    list.add(user);
//                                }
//                            }
                        }
                        adapter = new UserAdapter(list, FriendActivity.this, new UserInterface() {
                            @Override
                            public void privateMessages(User model) {

                            }

                            @Override
                            public void directChat(User model) {

                            }

                            @Override
                            public void add(User model) {

                            }
                        });
                        rv.setAdapter(adapter);
                        rv.setLayoutManager(new LinearLayoutManager(FriendActivity.this));
                        rv.setHasFixedSize(true);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}