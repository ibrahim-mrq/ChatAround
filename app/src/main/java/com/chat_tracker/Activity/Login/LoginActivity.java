package com.chat_tracker.Activity.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEtLoginEmail;
    private TextInputEditText mEtLoginPassword;
    private AppCompatTextView mTvNewAccount;
    private Button mBtnLogin;
    private FirebaseAuth mAuth;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private double lat2, lon2;
    private int LOCATION_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Toast.makeText(LoginActivity.this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
                return;
            }
            for (Location location : locationResult.getLocations()) {
                lat2 = location.getLatitude();
                lon2 = location.getLongitude();
            }
        }
    };

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

        /*    TODO : Location      */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                if (lat2 != 0 && lon2 != 0) {
                    setEnabled(false);
                    signIn();
                } else {
                    setEnabled(true);
                    checkSettingAndStartLocationUpdate();
                    Toast.makeText(LoginActivity.this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
                }
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

    private void setEnabled(boolean enabled) {
        mEtLoginEmail.setEnabled(enabled);
        mEtLoginPassword.setEnabled(enabled);
        mBtnLogin.setEnabled(enabled);
        mTvNewAccount.setEnabled(enabled);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            StartLocationUpdate();
        } else {
            askLocationPermission();
        }
        checkSettingAndStartLocationUpdate();
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("TAG", "askLocationPermission : You Should show an alert dialog... ");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    , LOCATION_REQUEST_CODE);
        }
    }

    private void checkSettingAndStartLocationUpdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsRequestTask = client.checkLocationSettings(request);
        locationSettingsRequestTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                StartLocationUpdate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(LoginActivity.this, LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void StartLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            onClick();
        }
    }

    private void StopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopLocationUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingAndStartLocationUpdate();
            } else {
                Toast.makeText(this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}