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
import androidx.core.view.GravityCompat;

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
    TextView name, phone, email, change, first_name,license, model;
    private DatabaseReference databaseReference,vehicleref;
    private FirebaseAuth mAuth;
    ImageView image;
    Button back;
    Uri imageUri;
    FirebaseStorage storage;
    FirebaseFirestore fStore;
    StorageReference profileRef;
    StorageReference storageReference;
    private String name1, email1, phone1,license1, model1;
    private long backpressed;
    private Toast backToast;


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
        image = findViewById(R.id.profile);
        change = findViewById(R.id.change_profile);

        first_name = findViewById(R.id.first_name);

        Intent intent = getIntent();
        name1 = intent.getStringExtra("name");
        email1 = intent.getStringExtra("email");
        phone1 = intent.getStringExtra("phone");
        //license1 = intent.getStringExtra("License Plate number");
        model1 = intent.getStringExtra("Make");
        storageReference = FirebaseStorage.getInstance().getReference();

        name.setText(name1);
        email.setText(email1);
        phone.setText("(+1)-" + phone1);
        first_name.setText(name1);

        vehicleref = FirebaseDatabase.getInstance().getReference().child("Kids").child(name1).child("Vehicles");
        vehicleref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot a : snapshot.getChildren()) {

                    license1 =a.child("License Plate number").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

//        license.setText(license1);
//        model.setText(model1);
        profileRef = storageReference.child("users/" + phone1);




        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(image);
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();


                uploadImageToFirebase(imageUri);


            }

        }
    }
    @Override
    public void onBackPressed() {
        if(backpressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;

        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backpressed = System.currentTimeMillis();

    }
    private void uploadImageToFirebase(Uri imageUri) {



        assert mAuth != null;
        StorageReference fileRef= storageReference.child("users/" + phone1);

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(image);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });






    }}