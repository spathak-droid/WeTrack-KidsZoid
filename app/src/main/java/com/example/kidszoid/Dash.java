package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
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

    SharedPreferences sharedPreferences;
    public static final String fileName = "login";
    public static final String Username = "username";
    public static final String Passwoord = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash);

        login = findViewById(R.id.button);
        password = findViewById(R.id.pass2);
        user = (findViewById(R.id.username1));

        //shared preferences code
//        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        if(sharedPreferences.contains(Username)){
//            Intent i = new Intent(Dash.this, SchoolScreen.class);
//            startActivity(i);
//        }


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
        Query checkUser = reference.orderByChild("phone").equalTo(userEntered);

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

                        String namedDB = snapshot.child(userEntered).child("name").getValue(String.class);
                        String phoneDB = snapshot.child(userEntered).child("phone").getValue(String.class);
                        String emailDB = snapshot.child(userEntered).child("email").getValue(String.class);

                        String aa = "";
                        if(emailDB.length() > 4)
                        {
                            aa = emailDB.substring(emailDB.length() - 4);
                        }
                        if (aa.equals(".edu"))
                        {
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(Username,userEntered);
//                            editor.putString(Passwoord,passEntered);
//                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), SchoolDropDown.class);
                            intent.putExtra("email", emailDB);
                            intent.putExtra("name", namedDB);
                            intent.putExtra("phone", phoneDB);

                            startActivity(intent);
                        }else{
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(Username,userEntered);
//                            editor.putString(Passwoord,passEntered);
//                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), userSchoolChoose.class);
                            intent.putExtra("email", emailDB);
                            intent.putExtra("name", namedDB);
                            intent.putExtra("phone", phoneDB);

                            startActivity(intent);
                        }




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
