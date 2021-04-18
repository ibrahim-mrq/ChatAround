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
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText et_firstName, et_lastName, et_phone, et_date_of_birth, et_email, et_password;
    private AutoCompleteTextView et_gender;
    private AppCompatTextView mTvHaveAccount;
    private Button mBtnRegister;
    private CircleImageView registerImg;
    private String firstName, lastName, phone, date_of_birth, email, password, gender;
    private GeoPoint geoPoint;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private double lat2, lon2;
    private Uri uri;
    private int GALLERY_REQUEST_CODE = 10002;
    private ArrayList<String> list = new ArrayList<>();
    ////////////////////////////////////////////////////////

    private int LOCATION_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Toast.makeText(RegisterActivity.this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
                return;
            }
            for (Location location : locationResult.getLocations()) {
                lat2 = location.getLatitude();
                lon2 = location.getLongitude();
            }
            geoPoint = new GeoPoint(lat2, lon2);
        }
    };

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        spEditor = sp.edit();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        find();
        getDataGender();
        onClick();
//        permission();

        /*    TODO : Location      */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*  *************************************/

    }


    private void permission() {

        Dexter.withContext(RegisterActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
    }

    private void find() {
        et_firstName = findViewById(R.id.et_register_first_name);
        et_lastName = findViewById(R.id.et_register_last_name);
        et_phone = findViewById(R.id.et_register_phone);
        et_gender = findViewById(R.id.register_et_gender);
        et_date_of_birth = findViewById(R.id.register_et_date_of_birth);
        et_email = findViewById(R.id.et_register_email);
        et_password = findViewById(R.id.et_register_password);

        registerImg = findViewById(R.id.registerImg);
        mBtnRegister = findViewById(R.id.btn_register);
        mTvHaveAccount = findViewById(R.id.tv_have_account);
    }

    private void onClick() {

        registerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
                if (lat2 != 0 && lon2 != 0) {
                    setEnabled(false);
                    if (uri == null) {
                        signup("");
                    } else
                        uploadImage();
                } else {
                    setEnabled(true);
                    checkSettingAndStartLocationUpdate();
                    Toast.makeText(RegisterActivity.this, "يجب الحصول على صلاحية الموقع !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage() {
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference refImage = mStorageRef.child("profileImages/" + UUID.randomUUID().toString());
            refImage.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            refImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    signup(String.valueOf(uri));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "خطا " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void signup(final String url) {
        list.add("");
        geoPoint = new GeoPoint(lat2, lon2);
        if (isEmpty()) {
            setEnabled(false);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final String Uid = mAuth.getUid() + "";
                                Map<String, Object> user = new HashMap<>();
                                user.put("token", Uid);
                                user.put("name", firstName + " " + lastName);
                                user.put("gender", gender);
                                user.put("phone", phone);
                                user.put("password", password);
                                user.put("dateOfBirth", date_of_birth);
                                user.put("email", email);
                                user.put("geoPoint", geoPoint);
                                user.put("friends", list);
                                user.put("isOnline", true);
                                user.put("image", url + "");
                                db.collection("User").document(Uid).set(user);
                                spEditor.putString("token", Uid);
                                spEditor.apply();
                                spEditor.commit();
                                Toast.makeText(RegisterActivity.this, "Registration  Successful.", Toast.LENGTH_SHORT).show();
                                privacyPolicyDialog();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                mBtnRegister.setEnabled(true);
                            }
                        }
                    });
        } else setEnabled(true);
    }

    private boolean isEmpty() {

        firstName = et_firstName.getText().toString();
        lastName = et_lastName.getText().toString();
        phone = et_phone.getText().toString();
        password = et_password.getText().toString();
        date_of_birth = et_date_of_birth.getText().toString();
        gender = et_gender.getText().toString();
        email = et_email.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            et_firstName.setError("Please Enter First Name");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            et_lastName.setError("Please Enter Last Name");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            et_phone.setError("Please Enter phone");
            return false;
        }
        if (TextUtils.isEmpty(date_of_birth)) {
            et_date_of_birth.setError("Please Enter date of birth");
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            et_gender.setError("Please Enter gender");
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
        et_firstName.setEnabled(enabled);
        et_lastName.setEnabled(enabled);
        et_gender.setEnabled(enabled);
        et_phone.setEnabled(enabled);
        et_date_of_birth.setEnabled(enabled);
        mBtnRegister.setEnabled(enabled);
        mTvHaveAccount.setEnabled(enabled);
    }

    private void getDataGender() {
        ArrayList<String> listGender = new ArrayList<>();
        listGender.add("Male");
        listGender.add("Female");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listGender);
        et_gender.setAdapter(adapter);
    }

    private void privacyPolicyDialog() {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "https://localhost/terms",
                "https://localhost/privacy");
        dialog.forceReset();
//        dialog.setTermsOfServiceSubtitle("asd");
//        dialog.setTitle("dfrgasd");

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
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
        dialog.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        apiException.startResolutionForResult(RegisterActivity.this, LOCATION_REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                uri = data.getData();
                registerImg.setImageURI(uri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}