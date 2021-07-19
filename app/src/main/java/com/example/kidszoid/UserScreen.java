package com.example.kidszoid;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class UserScreen extends AppCompatActivity {
    ImageView imageView;
    ImageView logOff;
    TextView name;
    Dialog dialog;
    FirebaseStorage storage;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri imageUri;
    StorageReference storageReference;
    Button go_to_profile;
    private String name1, email, phone, school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);

        logOff = findViewById(R.id.logoff);
        logOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout(v);}});
        name    =findViewById(R.id.full_name_btn);
        imageView = findViewById(R.id.image);
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        go_to_profile =findViewById(R.id.profile_view);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        //StorageReference profileRef = storageReference.child("image/*");
        Intent intent = getIntent();
        String name1 = intent.getStringExtra("name");
        name.setText(name1);



        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        go_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, userprofile.class);
                intent.putExtra("school", school);
                intent.putExtra("email", email);
                intent.putExtra("name", name1);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });


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



}
