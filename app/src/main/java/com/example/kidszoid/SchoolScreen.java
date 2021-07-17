package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SchoolScreen extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView logOff;
    Location current;
    Dialog dialog;
    private long backpressed;
    private Toast backToast;

    private String name, email, phone, school;



    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();

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

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();






        if (ActivityCompat.checkSelfPermission(SchoolScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            //when permisson denied
            //request permission
            ActivityCompat.requestPermissions(SchoolScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
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
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    current = location;
                    supportMapFragment.getMapAsync(SchoolScreen.this);
                }else{
                    ActivityCompat.requestPermissions(SchoolScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                }
            }
        });

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
        if (requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
        MarkerOptions options = new MarkerOptions().position(latLng).title("You are Here!");
        //zoom map
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

        //Add marker on map
        googleMap.addMarker(options);

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

        }
        return true;
    }
}