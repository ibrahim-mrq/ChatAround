package com.chat_tracker.Activity.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText et_name, et_phone, et_email, et_password;
    private AppCompatTextView mTvHaveAccount;
    private TextView tv_location;
    private Button mBtnRegister;
    private String name, phone, email, password;
    private GeoPoint geoPoint;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private double lat, lon;
    private GoogleMap mMap;
    private Dialog dialog;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_place);

        sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        spEditor = sp.edit();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        find();
        permission();
        onClick();
    }

    private void permission() {

        Dexter.withContext(RegisterPlaceActivity.this)
                .withPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                finish();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void find() {
        et_name = findViewById(R.id.et_registerPlace_name);
        et_phone = findViewById(R.id.et_registerPlace_phone);
        et_email = findViewById(R.id.et_registerPlace_email);
        et_password = findViewById(R.id.et_registerPlace_password);
        mBtnRegister = findViewById(R.id.btn_registerPlace);
        mTvHaveAccount = findViewById(R.id.registerPlace_tv_have_account);
        tv_location = findViewById(R.id.et_registerPlace_location);
    }

    private void onClick() {

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogs();
            }
        });
        mTvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnabled(false);
                if (lat != 0 && lon != 0) {
                    signup();
                } else {
                    setEnabled(true);
                    Toast.makeText(RegisterPlaceActivity.this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogs() {
        dialog = new Dialog(RegisterPlaceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_map);
        MapView mMapView = dialog.findViewById(R.id.mapView2);
        Button button4 = dialog.findViewById(R.id.button4);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(RegisterPlaceActivity.this);
        dialog.show();
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_location.setText("" + lat + "," + lon);
                dialog.dismiss();
            }
        });
    }

    private void signup() {
        list.add("");
        geoPoint = new GeoPoint(lat, lon);
        if (isEmpty()) {
            setEnabled(false);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterPlaceActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final String Uid = mAuth.getUid() + "";
                                Map<String, Object> user = new HashMap<>();
                                user.put("token", Uid);
                                user.put("name", name);
                                user.put("gender", "");
                                user.put("phone", phone);
                                user.put("password", password);
                                user.put("dateOfBirth", "");
                                user.put("email", email);
                                user.put("friends", list);
                                user.put("geoPoint", geoPoint);
                                user.put("isOnline", true);
                                user.put("image", "");
                                db.collection("Place").document(Uid).set(user);
                                spEditor.putString("token", Uid);
                                spEditor.apply();
                                spEditor.commit();
                                privacyPolicyDialog();
                            } else {
                                Toast.makeText(RegisterPlaceActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                mBtnRegister.setEnabled(true);
                            }
                        }
                    });
        } else setEnabled(true);
    }

    private boolean isEmpty() {

        name = et_name.getText().toString();
        phone = et_phone.getText().toString();
        password = et_password.getText().toString();
        email = et_email.getText().toString();

        if (TextUtils.isEmpty(name)) {
            et_name.setError("Please Enter First Name");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            et_phone.setError("Please Enter phone");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please Enter Email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please Enter password");
            return false;
        }
        if (password.length() < 6) {
            et_password.setError("password to short");
            return false;
        }
        return true;
    }

    private void setEnabled(boolean enabled) {
        et_email.setEnabled(enabled);
        et_password.setEnabled(enabled);
        et_name.setEnabled(enabled);
        et_phone.setEnabled(enabled);
        mBtnRegister.setEnabled(enabled);
        mTvHaveAccount.setEnabled(enabled);
    }

    private void privacyPolicyDialog() {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "https://localhost/terms",
                "https://localhost/privacy");
        dialog.forceReset();
//        dialog.setTermsOfServiceSubtitle("asd");
//        dialog.setTitle("dfrgasd");

        dialog.addPoliceLine("This application uses a unique user identifier for advertising purposes, it is shared with third-party companies.");
        dialog.addPoliceLine("This application sends error reports, installation and send it to a server of the Fabric.io company to analyze and process it.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Installed applications and history of installed applications, ip address, unique installation id, token to send notifications, version of the application, time zone and information about the language of the device.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");
        dialog.addPoliceLine("This application uses a unique user identifier for advertising purposes, it is shared with third-party companies.");
        dialog.addPoliceLine("This application sends error reports, installation and send it to a server of the Fabric.io company to analyze and process it.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Installed applications and history of installed applications, ip address, unique installation id, token to send notifications, version of the application, time zone and information about the language of the device.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");
        dialog.addPoliceLine("This application uses a unique user identifier for advertising purposes, it is shared with third-party companies.");
        dialog.addPoliceLine("This application sends error reports, installation and send it to a server of the Fabric.io company to analyze and process it.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Installed applications and history of installed applications, ip address, unique installation id, token to send notifications, version of the application, time zone and information about the language of the device.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");

        dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean isFirstTime) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                Toast.makeText(RegisterPlaceActivity.this, "Registration  Successful.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                lat = latLng.latitude;
                lon = latLng.longitude;
                tv_location.setText("" + lat + "," + lon);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                );
            }
        });
    }
}