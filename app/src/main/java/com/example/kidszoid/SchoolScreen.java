package com.example.kidszoid;

import androidx.annotation.NonNull;
import android.os.Handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class SchoolScreen extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener {

    DrawerLayout drawerLayout;
    boolean doublePress;
    private FirebaseAuth auth;
    NavigationView navigationView;

    GoogleMap mMap;
    MarkerOptions place1, place2, schoolPlace;
    Toolbar toolbar;
    String kids_name, plate_name, vehicle_name, grade;
    ArrayList<String> kidd;
    ImageView logOff;
    //Location current;
    Dialog dialog;
    private long backpressed;
    private Toast backToast;

    private String name, email, phone, school, userID;
    Marker userLocationMarker;



    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();
        kidd = new ArrayList<String>();


        drawerLayout = findViewById(R.id.drawer_layout);
        logOff = findViewById(R.id.logoff);
        logOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout(v);}});
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_action);
        //setSupportActionBar(toolbar);


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        school = intent.getStringExtra("school");
        userID = school;

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //getCurrentLocation();






        if (ActivityCompat.checkSelfPermission(SchoolScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            //when permisson denied
            //request permission
            ActivityCompat.requestPermissions(SchoolScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }






    }



    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };


    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }}

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            // you need to request permissions...
        }
    }





        public void logout( View view){
        dialog = new Dialog(SchoolScreen.this);
        dialog.setContentView(R.layout.pop_up);

        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pop));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button yes = dialog.findViewById(R.id.btn_yes);
        Button no = dialog.findViewById(R.id.btn_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss(); }});
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), Dash.class);
                startActivity(intent);
            }});
        dialog.show(); }



        private void getCurrentLocation() {
        //task location
        if (ActivityCompat.checkSelfPermission(SchoolScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SchoolScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //current = location;
                    supportMapFragment.getMapAsync(SchoolScreen.this);



                }else{
                    ActivityCompat.requestPermissions(SchoolScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                }
            }
        });

    }

    private void getParentLocation() {

        school = getIntent().getStringExtra("school");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference schoolref = databaseReference.child("parentAvailable").child(school);


        schoolref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot a : snapshot.getChildren()) {
                    String ParentName = a.getKey();


                    LatLng latLng = new LatLng(a.child("l").child("0").getValue(Double.class),
                            a.child("l").child("1").getValue(Double.class));
                    schoolPlace = new MarkerOptions().position(latLng).title(ParentName).icon(bitmapDescriptor(getApplicationContext(), R.drawable.car_marker));
                    Marker marker = mMap.addMarker(schoolPlace);
                    marker.showInfoWindow();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });}

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("schoolAvailable");
//
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.removeLocation(userID);

    }

    @Override
    public void onBackPressed() {
        if(backpressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backpressed = System.currentTimeMillis();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location current = new Location("");
        if(school.equals("Arlington High School")){
            LatLng latLng = new LatLng(32.72026102960085, -97.11733558736796);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Arlington High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);
        }else if(school.equals("Arlington Collegiate High School")){
            LatLng latLng = new LatLng(32.64252393175996, -97.06792151805416);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Arlington Collegiate High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);
        }else if(school.equals("Lamar High School")){
            LatLng latLng = new LatLng(32.763895398417866, -97.12546380086025);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Lamar High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);
        }else if(school.equals("James Martin High School")){
            LatLng latLng = new LatLng(32.684742600649024, -97.17997192969774);
            MarkerOptions options = new MarkerOptions().position(latLng).title("James Martin High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);
        }else if(school.equals("Premier High School")){
            LatLng latLng = new LatLng(32.77301696770131, -97.10497080001343);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Premier High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);

        }else if(school.equals("James Bowie High School")){
            LatLng latLng = new LatLng(32.6636262069267, -97.0745371027115);
            MarkerOptions options = new MarkerOptions().position(latLng).title("James Bowie High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);

        }else if(school.equals("Sam Houston High School")){
            LatLng latLng = new LatLng(32.70281152861373, -97.07571061620396);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Sam Houston High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);

        }else if(school.equals("Juan Seguin High School")){
            LatLng latLng = new LatLng(32.63185637556569, -97.09968984688997);
            MarkerOptions options = new MarkerOptions().position(latLng).title("Juan Seguin High School").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);

        }else{
            LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
            MarkerOptions options = new MarkerOptions().position(latLng).title("You are Here!").
                    icon(bitmapDescriptor(getApplicationContext(),R.drawable.school_marker));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            googleMap.addMarker(options);
            current.setLatitude(latLng.latitude); current.setLongitude(latLng.longitude);
        }
        getParentLocation();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("schoolAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userID, new GeoLocation(current.getLatitude(), current.getLongitude()));
        googleMap.setOnMarkerClickListener(this);
    }
    private BitmapDescriptor bitmapDescriptor(Context context, int vector){
        Drawable vectordrawable = ContextCompat.getDrawable(context,vector);
        vectordrawable.setBounds(0,0,vectordrawable.getIntrinsicWidth(),
                vectordrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectordrawable.getIntrinsicWidth(),
                vectordrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectordrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                dialog = new Dialog(SchoolScreen.this);
                dialog.setContentView(R.layout.pop_up);

                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pop));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                Button yes = dialog.findViewById(R.id.btn_yes);
                Button no = dialog.findViewById(R.id.btn_no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {dialog.dismiss(); }});
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auth.signOut();
                        Intent intent = new Intent(getApplicationContext(), Dash.class);
                        startActivity(intent);
                    }});
                dialog.show();
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(SchoolScreen.this, SchoolProfile.class);
                intent.putExtra("school", school);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
            case R.id.nav_rate:
                dialog = new Dialog(SchoolScreen.this);
                dialog.setContentView(R.layout.rating);

                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pop));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                Button submit = dialog.findViewById(R.id.btn_submit);
                RatingBar bar = dialog.findViewById(R.id.rating_bar);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = String.valueOf(bar.getRating());
                        if(!s.equals("0.0")){
                        Toast.makeText(getApplicationContext(), s+" stars. " +"Thank you !!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();}
                        else{dialog.dismiss();}
                    }
                });
                dialog.show();
                break;
            case R.id.nav_history:
                Intent in = new Intent(SchoolScreen.this, chooseDate.class);
                in.putExtra("school", school);
                startActivity(in);
                break;



        }
        return true;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
                String marker_name = marker.getTitle();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Kids");
                DatabaseReference kidsRef = databaseReference.child(marker_name).child("Kids");
                DatabaseReference VehicleRef = databaseReference.child(marker_name).child("Vehicles");
                if(kidsRef != null && VehicleRef!=null){
                    kidsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot Kidsnapshot) {
                            for(DataSnapshot bb :Kidsnapshot.getChildren()){
                                kidd.add(bb.getKey().toUpperCase());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});

                    VehicleRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot Kidsnapshot) {
                            for(DataSnapshot cc :Kidsnapshot.getChildren()){
                                vehicle_name = cc.child("Make").getValue().toString().toUpperCase() + ", " + cc.child("Model").getValue().toString().toUpperCase();
                                plate_name = cc.child("License Plate number").getValue().toString().toUpperCase();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});


                if(doublePress){
                    kids_name = " ";
                    if (kidd != null){
                    for (String s : kidd){
                        kids_name += s + " & ";}
                    }
                    kids_name = kids_name.substring(0, (kids_name.length() - 3));
                    dialog = new Dialog(SchoolScreen.this);
                    dialog.setContentView(R.layout.checkparent);

                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.showlisc));
                    //dialog.getWindow().setLayout(ViewGroup.LayoutParams.M, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(true);

                    TextView vehicle = dialog.findViewById(R.id.vehicle_pop);
                    TextView plate = dialog.findViewById(R.id.plate_pop);
                    TextView kid = dialog.findViewById(R.id.kids_pop);
                    Button button = dialog.findViewById(R.id.btn_cc);
                    button.setVisibility(View.GONE);

                    Date current_time = Calendar.getInstance().getTime();
                    SimpleDateFormat s = new SimpleDateFormat("hh:mm a");
                    SimpleDateFormat date_s = new SimpleDateFormat("MM-dd-yyyy");
                    String time = s.format(current_time);
                    String date = date_s.format(current_time);
                    //String format = DateFormat.getDateInstance(DateFormat.FULL).format(current_time);

                    vehicle.setText(vehicle_name);
                    plate.setText(plate_name);
                    kid.setText(kids_name);

                    kidd = new ArrayList<>();
                    kids_name= "";
                    CheckBox dropoff = dialog.findViewById(R.id.drop_check);
                    CheckBox pickup = dialog.findViewById(R.id.pick_check);
                    dropoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            pickup.setChecked(false);
                            button.setVisibility(View.VISIBLE);
                        }
                    });
                    pickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            dropoff.setChecked(false);
                            button.setVisibility(View.VISIBLE);
                        }
                    });
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pickup.isChecked()){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("History").child(userID).child(date).child("pickup").child(kid.getText().toString());
                                ref.setValue(time);
                                dialog.dismiss();
                                marker.setVisible(false);

                            }else if (dropoff.isChecked()){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("History").child(userID).child(date).child("dropoff").child(kid.getText().toString());
                                ref.setValue(time);
                                dialog.dismiss();
                                marker.setVisible(false);

                            }
                        }
                    });
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();

                }else{
                    kidd = new ArrayList<>();
                    Toast.makeText(SchoolScreen.this, "Please Click Again to View Info", Toast.LENGTH_SHORT).show();
                    this.doublePress = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doublePress = false;
                        }
                    }, 2000);
                }}


                return false;
            }


    }
