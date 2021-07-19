package com.example.kidszoid;

import android.renderscript.Sampler;
import android.util.Log;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.UUID;
public class userprofile extends AppCompatActivity {
    TextView name, phone, email, school, first_name;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        name = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone_no);
        email = findViewById(R.id.email_user);

        first_name = findViewById(R.id.first_name);

        Intent intent = getIntent();
        String name1 = intent.getStringExtra("name");
        String email1 = intent.getStringExtra("email");
        String phone1 = intent.getStringExtra("phone");


        name.setText(name1);
        email.setText(email1);
        phone.setText("(+1)-"+phone1);
        first_name.setText(name1);



//        Intent intent = getIntent();
//        String name1 = intent.getStringExtra("name");
//        String email1 = intent.getStringExtra("email");
//        String phone1 = intent.getStringExtra("phone");


//        name.setText(name1);
//        email.setText(email1);
//        phone.setText("(+1)-" + phone1);
//
//        first_name.setText(name1);
    }

}