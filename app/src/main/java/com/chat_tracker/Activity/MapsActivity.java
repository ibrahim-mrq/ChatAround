package com.chat_tracker.Activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat_tracker.Activity.Login.LoginActivity;
import com.chat_tracker.Activity.Login.ProfileActivity;
import com.chat_tracker.Adapter.UserAdapter;
import com.chat_tracker.Interface.UserInterface;
import com.chat_tracker.Model.User;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private Button button;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private ArrayList<User> list = new ArrayList<>();
    private UserAdapter adapter;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<User> listUser = new ArrayList<>();
    private ArrayList<String> listId = new ArrayList<>();
    private TextView textView;
    private String token;
    private String name;
    private String img;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private RecyclerView rv;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sp = getSharedPreferences("ChatTracker", MODE_PRIVATE);
        spEditor = sp.edit();
        token = sp.getString("token", "token");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rv = findViewById(R.id.map_rv);
        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView2);
        textView.setTextColor(Color.GREEN);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ProfileActivity.class)
                        .putExtra("token", token)
                        .putExtra("name", name)
                        .putExtra("img", img)
                        .putStringArrayListExtra("listId", listId)
                );
            }
        });
        getDataUser();
        getData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                spEditor.clear();
                spEditor.apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        geocoder = new Geocoder(this);
        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        bottomSheet();
    }

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                DocumentReference noteRef = db.collection("User").document(token.trim());
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                noteRef.update("geoPoint", geoPoint);
                getData();
                getDataUser();
            }
        }
    };

    private void bottomSheet() {
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet_behavior_id);
        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setHideable(false);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    int finalI = 0;

    Bitmap bitmap1;

    private void getData() {
        db.collection("User")
                .orderBy("name")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable final FirebaseFirestoreException e) {
                        list = new ArrayList<>();
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0 && !listUser.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                user.setType("User");
                                LatLng a1 = new LatLng(listUser.get(0).getGeoPoint().getLatitude(), listUser.get(0).getGeoPoint().getLongitude());
                                LatLng a2 = new LatLng(user.getGeoPoint().getLatitude(), user.getGeoPoint().getLongitude());
                                if (CalculationByDistance(a1, a2) <= 5 && !user.getToken().equals(token)) {
                                    if (user.getIsOnline()) {
                                        list.add(user);
                                    }
                                }
                            }
                            adapter = new UserAdapter(list, MapsActivity.this, new UserInterface() {
                                @Override
                                public void privateMessages(User model) {
                                    if (listUser.get(0).getFriends().contains(model.getToken())) {
                                        Intent intent = new Intent(MapsActivity.this, ChatActivity.class);
                                        intent.putExtra("TokenTwo", model.getToken());
                                        intent.putExtra("name", model.getName());
                                        intent.putExtra("userSender", name);
                                        intent.putExtra("type", model.getType());
                                        intent.putExtra("messages", "privateMessages");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MapsActivity.this, "ليس من الاصدقاء !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void directChat(User model) {
                                    Intent intent = new Intent(MapsActivity.this, ChatActivity.class);
                                    intent.putExtra("TokenTwo", model.getToken());
                                    intent.putExtra("name", model.getName());
                                    intent.putExtra("userSender", name);
                                    intent.putExtra("type", model.getType());
                                    intent.putExtra("messages", "directChat");
                                    startActivity(intent);
                                }

                                @Override
                                public void add(User model) {
                                    if (listUser.get(0).getFriends().contains(model.getToken())) {
                                        Toast.makeText(MapsActivity.this, " الاصدقاء !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DocumentReference noteRef = db.collection("User").document(token.trim());
                                        noteRef.update("friends", FieldValue.arrayUnion(model.getToken()));
                                        DocumentReference noteRef2 = db.collection("User").document(model.getToken().trim());
                                        noteRef2.update("friends", FieldValue.arrayUnion(token.trim()));
                                        Toast.makeText(MapsActivity.this, " تم الاضافة !", Toast.LENGTH_SHORT).show();
//                                        getData();
                                    }
                                }
                            });
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
                            rv.setHasFixedSize(true);
                            adapter.notifyDataSetChanged();
                            if (mMap != null) {
                                mMap.clear();
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                mMap.setTrafficEnabled(true);
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                                mMap.getUiSettings().setCompassEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap.getUiSettings().setAllGesturesEnabled(true);
                                mMap.setMyLocationEnabled(true);

                                try {
                                    final LatLng a1 = new LatLng(listUser.get(0).getGeoPoint().getLatitude(),
                                            listUser.get(0).getGeoPoint().getLongitude());
                                    for (int i = 0; i < list.size(); i++) {
                                        Log.d("getGeoPoint", list.get(i).getGeoPoint() + "");
                                        double lat = list.get(i).getGeoPoint().getLatitude();
                                        double lon = list.get(i).getGeoPoint().getLongitude();
                                        final LatLng a2 = new LatLng(lat, lon);
                                        if (CalculationByDistance(a1, a2) <= 5 && !list.get(i).getToken().equals(token)) {
                                            Target target = new Target() {
                                                @Override
                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                    bitmap1 = bitmap;
                                                }

                                                @Override
                                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                                }

                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                }

                                            };
                                            Picasso.get().load(list.get(i).getImage())
                                                    .resize(60, 60)
                                                    .centerInside()
                                                    .into(target);
                                            try {
//                                                Bitmap bitmap = createUserBitmap();
                                                mMap.addMarker(new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(bitmap1)))
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                                                        .position(new LatLng(lat, lon))
                                                        .snippet("" + list.get(i).getToken())
                                                        .title("" + list.get(i).getName()));
                                            } catch (Exception e1) {
                                                mMap.addMarker(new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                                                        .position(new LatLng(lat, lon))
                                                        .snippet("" + list.get(i).getToken())
                                                        .title("" + list.get(i).getName()));
                                            }
                                        }
                                        mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                                .position((a1)).title("" + listUser.get(0).getName()).snippet(" ( Me ) ")
                                        );
                                        mMap.addCircle(new CircleOptions()
                                                .center(a1)
                                                .strokeWidth(3)
                                                .radius(5000)
                                                .strokeColor(Color.parseColor("#10393F")));

                                        finalI = i;
                                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            @Override
                                            public void onInfoWindowClick(Marker marker) {
                                                marker.setTag("marker");
                                                if (!marker.getPosition().equals(a1)) {
                                                    Intent intent = new Intent(MapsActivity.this, ChatActivity.class);
                                                    intent.putExtra("TokenTwo", marker.getSnippet());
                                                    intent.putExtra("name", marker.getTitle());
                                                    intent.putExtra("type", list.get(finalI).getType());
                                                    intent.putExtra("messages", "directChat");
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception ee) {
//                                    LatLng Gaza = new LatLng(31.5200407, 34.4576236);
//                                    mMap.addMarker(new MarkerOptions().position(Gaza).title("Gaza user"));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Gaza));
                                }
                            }
                        }
                    }
                });
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    private void getDataUser() {
        listUser = new ArrayList<>();
        db.collection("User").whereEqualTo("token", token)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                listId.addAll(user.getFriends());
                                name = user.getName();
                                img = user.getImage();
                                listUser.add(user);
                                textView.setText(user.getName());
                            }
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Toast.makeText(MapsActivity.this, "asd", Toast.LENGTH_SHORT).show();
                Log.d("PointOfInterest", poi.name + " " + poi.placeId + " " + poi.latLng + " ");
            }
        });
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 5000;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d("results", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec + " :Radius :" + Radius * c);

//        return Radius * c;
        return kmInDec;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            StartLocationUpdate();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    , ACCESS_LOCATION_REQUEST_CODE);
        }
        checkSettingAndStartLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopLocationUpdate();
    }

    private void StartLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void StopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        DocumentReference noteRef = db.collection("User").document(token.trim());
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        noteRef.update("geoPoint", geoPoint);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingAndStartLocationUpdate();
                enableUserLocation();
//                getData();
//                getDataUser();
            } else {
                Toast.makeText(this, "asd", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , ACCESS_LOCATION_REQUEST_CODE);
            }
        }
    }

    private void checkSettingAndStartLocationUpdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsRequestTask = client.checkLocationSettings(request);
        locationSettingsRequestTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
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