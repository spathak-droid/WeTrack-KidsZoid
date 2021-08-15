package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class license extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int Gallery_code = 1000;
    ImageView selectedImage, camera, gallery;
    private StorageReference mStorageRef;
    String phone, name;
    private long backpressed;
    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        selectedImage = findViewById(R.id.selected_image);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();

        name = intent.getStringExtra("name");

        StorageReference profileRef = mStorageRef.child("License/"+name);

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(selectedImage);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(license.this,"Camera Clicked",Toast.LENGTH_SHORT).show();
                askCameraPermission();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(license.this,"Gallery Clicked",Toast.LENGTH_SHORT).show();
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, Gallery_code);
            }
        });


    }
    @Override
    public void onBackPressed() {
        if (backpressed + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
//        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backpressed = System.currentTimeMillis();

    }


    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[ ]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else
        {
            openCamera();
        }
    }


    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_code) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                //imageView.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);


            }

        }
        if(resultCode== Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                onCaptureOImageResult(data);
//                Bitmap image = (Bitmap) data.getExtras().get("data");
//                selectedImage.setImageBitmap(image);

            }
        }
    }


    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = mStorageRef.child("License/"+name);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(selectedImage);
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



    private void onCaptureOImageResult(Intent data) {
        Bitmap thumbnail =(Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        byte bb[] =bytes.toByteArray();

        selectedImage.setImageBitmap(thumbnail);
        uploadToFirebase(bb);
    }

    private void uploadToFirebase(byte[] bb) {
        StorageReference sr =mStorageRef.child("License/"+name);
        sr.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "SuccessFully Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==CAMERA_PERM_CODE)
        {
            if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)//open camera
            {
                openCamera();
            }
            else {
                Toast.makeText(this,"Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}