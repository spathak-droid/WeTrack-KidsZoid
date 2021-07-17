package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class SchoolProfile extends AppCompatActivity {
    TextView name, phone, email, school, first_name;
    ImageView profile;
    TextView frame;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private  String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        storageProfile = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        profile = findViewById(R.id.profile);
        frame = findViewById(R.id.change_profile);


        name = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone_no);
        email = findViewById(R.id.email_user);
        school = findViewById(R.id.school);
        first_name = findViewById(R.id.first_name);

        Intent intent = getIntent();
        String name1 = intent.getStringExtra("name");
        String email1 = intent.getStringExtra("email");
        String phone1 = intent.getStringExtra("phone");
        String school1 = intent.getStringExtra("school");

        name.setText(name1);
        email.setText(email1);
        phone.setText("(+1)-"+phone1);
        school.setText(school1);
        first_name.setText(name1);

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
                CropImage.activity().setAspectRatio(1,1).start(SchoolProfile.this);
                getUserinfo();
            }
        });

    }

    private void getUserinfo(){
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()&& snapshot.getChildrenCount()>0){
                    if(snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profile.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "ERROR, TRY AGAIN!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set Your profile");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef = storageProfile.child(mAuth.getCurrentUser().getUid()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);
                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                    }

                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "Please Select An Image", Toast.LENGTH_SHORT).show();
        }
    }
}
