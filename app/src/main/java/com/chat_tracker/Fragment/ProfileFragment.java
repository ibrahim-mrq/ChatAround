package com.chat_tracker.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.chat_tracker.Model.User;
import com.chat_tracker.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {

    private View v;
    private TextInputEditText profileFEtName, profileFEtPhone, profileFEtDateOfBirth, profileFEtEmail, profileFEtPassword;
    private AutoCompleteTextView profileFEtGender;
    private TextInputLayout profileFTvPassword;
    private Button profileFBtn;
    private String name, phone, gender, dateOfBirth, email, password;
    private FirebaseFirestore db;
    private String token;
    private boolean isSelect = true;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();

        sp = getActivity().getSharedPreferences("ChatTracker", Context.MODE_PRIVATE);
        spEditor = sp.edit();


        token = getActivity().getIntent().getStringExtra("token");

        initView();
        getData();
        getDataGender();

        if (!sp.getString("token", "token").equals(token)) {
            profileFEtEmail.setVisibility(View.GONE);
            profileFEtPassword.setVisibility(View.GONE);
            profileFBtn.setVisibility(View.GONE);
            profileFTvPassword.setVisibility(View.GONE);
        } else {
            profileFEtEmail.setVisibility(View.VISIBLE);
            profileFEtPassword.setVisibility(View.VISIBLE);
            profileFTvPassword.setVisibility(View.VISIBLE);
            profileFBtn.setVisibility(View.VISIBLE);
        }

        setEnabled(false);
        profileFBtn.setText("تعديل");

        profileFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelect) {
                    isSelect = false;
                    setEnabled(true);
                    profileFBtn.setText("اضافة");
                } else {
                    isSelect = true;
                    profileFBtn.setText("تعديل");
                    setEnabled(false);
                }

            }
        });

        return v;

    }

    private void initView() {
        profileFEtName = v.findViewById(R.id.profileF_et_name);
        profileFEtPhone = v.findViewById(R.id.profileF_phone);
        profileFEtGender = v.findViewById(R.id.profileF_et_gender);
        profileFEtDateOfBirth = v.findViewById(R.id.profileF_et_date_of_birth);
        profileFEtEmail = v.findViewById(R.id.profileF_et_email);
        profileFEtPassword = v.findViewById(R.id.profileF_et_password);
        profileFTvPassword = v.findViewById(R.id.profileF_tv_password);
        profileFBtn = v.findViewById(R.id.profileF_btn);
    }

    private void getDataGender() {
        ArrayList<String> listGender = new ArrayList<>();
        listGender.add("Male");
        listGender.add("Female");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listGender);
        profileFEtGender.setText("Male");
        profileFEtGender.setAdapter(adapter);

    }

    private void getData() {
        db.collection("User").whereEqualTo("token", token)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                profileFEtName.setText(user.getName());
                                profileFEtDateOfBirth.setText(user.getDateOfBirth());
                                profileFEtEmail.setText(user.getEmail());
                                profileFEtPassword.setText(user.getPassword());
                                profileFEtPhone.setText(user.getPhone());
                                profileFEtGender.setText(user.getGender());
                            }
                        }
                    }
                });
    }

    private boolean isEmpty() {
        name = profileFEtName.getText().toString();
        phone = profileFEtPhone.getText().toString();
        password = profileFEtPassword.getText().toString();
        dateOfBirth = profileFEtDateOfBirth.getText().toString();
        gender = profileFEtGender.getText().toString();
        email = profileFEtEmail.getText().toString();

        if (TextUtils.isEmpty(name)) {
            profileFEtName.setError("Please Enter First Name");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            profileFEtPhone.setError("Please Enter phone");
            return false;
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            profileFEtDateOfBirth.setError("Please Enter date of birth");
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            profileFEtGender.setError("Please Enter gender");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            profileFEtEmail.setError("Please Enter Email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            profileFEtPassword.setError("Please Enter password");
            return false;
        }
        if (password.length() < 6) {
            profileFEtPassword.setError("password to short");
            return false;
        }
        return true;
    }

    private void setEnabled(boolean enabled) {
        profileFEtPassword.setEnabled(enabled);
        profileFEtName.setEnabled(enabled);
        profileFEtDateOfBirth.setEnabled(enabled);
        profileFEtEmail.setEnabled(enabled);
        profileFEtGender.setEnabled(enabled);
        profileFEtPhone.setEnabled(enabled);
    }

}