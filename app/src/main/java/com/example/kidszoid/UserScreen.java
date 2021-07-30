package com.example.kidszoid;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class UserScreen extends FragmentActivity implements  NavigationView.OnNavigationItemSelectedListener {
    ImageView imageView;

    NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ImageView logOff;
    //TextView name;
    Dialog dialog;
    FirebaseStorage storage;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri imageUri;
    StorageReference storageReference;
    Button go_to_profile,add_kid;
    private String name, email, phone;
    private long backpressed;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);

        logOff = findViewById(R.id.logoff);
        logOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });
        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_action1);

        // name    =findViewById(R.id.full_name_btn);
        imageView = findViewById(R.id.image);
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        go_to_profile = findViewById(R.id.profile_view);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        navigationView.bringToFront();
        // setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");


        go_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, userprofile.class);

                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
//        add_kid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserScreen.this, kids.class);
//                intent.putExtra("name", name);
//                startActivity(intent);
//            }
//        });


        StorageReference profileRef = storageReference.child("profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
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
        protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();

                    //imageView.setImageURI(imageUri);

                    uploadImageToFirebase(imageUri);


                }

            }
        }
    private void uploadImageToFirebase(Uri imageUri) {

        final StorageReference fileRef = storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void logout( View view){
        dialog = new Dialog(UserScreen.this);
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
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), Dash.class);
                startActivity(intent);
            }});
        dialog.show(); }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                dialog = new Dialog(UserScreen.this);
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
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), Dash.class);
                        startActivity(intent);
                    }});
                dialog.show();
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(UserScreen.this, userprofile.class);

                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;

            case R.id.nav_kid:
                Intent intent1 = new Intent(UserScreen.this, kids.class);
                intent1.putExtra("name", name);
                startActivity(intent1);

        }
        return true;
    }

}
