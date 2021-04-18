package com.chat_tracker.Activity.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEtLoginEmail;
    private TextInputEditText mEtLoginPassword;
    private AppCompatTextView mTvNewAccount;
    private Button mBtnLogin;
    private FirebaseAuth mAuth;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        spEditor = sp.edit();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }
        find();
        onClick();

    }


    private void find() {
        mEtLoginEmail = findViewById(R.id.et_login_email);
        mEtLoginPassword = findViewById(R.id.et_login_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvNewAccount = findViewById(R.id.tv_new_account);
    }

    private void onClick() {

        mTvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChooseRegisterActivity.class));
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        String email = mEtLoginEmail.getText().toString();
        String pass = mEtLoginPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            mEtLoginEmail.setError("Please Enter Your Email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            mEtLoginPassword.setError("Please Enter Your Password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            spEditor.putString("token", mAuth.getUid() + "");
                            spEditor.apply();
                            spEditor.commit();
                            Toast.makeText(LoginActivity.this, "signIn success.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "This email is not registered.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}