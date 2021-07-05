package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Dash extends AppCompatActivity {
    TextView register, click;
    TextInputLayout user, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash);

        login = findViewById(R.id.button);
        password = findViewById(R.id.pass2);
        user = (findViewById(R.id.username1));
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.isEnabled();
//            }
//        });
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dash.this, SignUp.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    loginUser(v);
                }

        });



    }
    private Boolean validateUsername (){
        if (user.getEditText().getText().toString().isEmpty()){
            user.setError("Field Cannot be Empty");
            return false;}
            else {
                user.setError(null);
                user.setErrorEnabled(false);
                return true;
            }
    }
    private Boolean validatePass (){
        if (password.getEditText().getText().toString().isEmpty()){
            password.setError("Field Cannot be Empty");
            return false;}
            else {
                password.setError(null);
                password.setErrorEnabled(false);
                return true;
            }
    }

    public void loginUser(View view){
        if (!validatePass() | !validateUsername()){
            return;
        }
        else{
            isUser();
        }
    }

    private void isUser() {
        String userEntered = user.getEditText().getText().toString().trim();
        String passEntered = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = reference.orderByChild("email").equalTo(userEntered);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    user.setError(null);
                    user.setErrorEnabled(false);
                    String passwordDB = snapshot.child(userEntered).child("password").getValue(String.class);

                    if(passwordDB.equals(passEntered)){
                        user.setError(null);
                        user.setErrorEnabled(false);

                        String namedDB = snapshot.child(passEntered).child("name").getValue(String.class);
                        String phoneDB = snapshot.child(passEntered).child("phone").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), UserScreen.class);
                        intent.putExtra("email", userEntered);
                        intent.putExtra("name", namedDB);
                        intent.putExtra("phone", phoneDB);

                        startActivity(intent);
                    }else{
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }

                }else{
                    user.setError("No Such User Exist. Please Register to Continue");
                    user.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
