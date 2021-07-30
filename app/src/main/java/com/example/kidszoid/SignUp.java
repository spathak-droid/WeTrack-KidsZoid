package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Helper;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    TextView click;
    TextInputLayout FN, email, phone, pass;
    Button sign;

    FirebaseDatabase root;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final ProgressBar progressBar = findViewById(R.id.progressbar);

        click = findViewById(R.id.logclick);
        FN = findViewById(R.id.logname);
        email = findViewById(R.id.logemail);
        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.logpass);
        sign = findViewById(R.id.sign);


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignUp.this, Dash.class);
                startActivity(intent);
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (FN.getEditText().getText().toString().trim().isEmpty() || email.getEditText().getText().toString().trim().isEmpty() ||
//                        phone.getEditText().getText().toString().trim().isEmpty() || pass.getEditText().getText().toString().trim().isEmpty()){
//                    Toast.makeText(SignUp.this, "Please Fill All the Boxes", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                int length = phone.getEditText().getText().length();
                int length2 = pass.getEditText().getText().length();

                if (FN.getEditText().getText().toString().trim().isEmpty()) {
                    FN.setError(" ");
                    FN.requestFocus();
                    return;}
                else{ FN.setError(null);}

                String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.[a-z]+";
                if (email.getEditText().getText().toString().trim().isEmpty()) {
                    email.setError(" ");
                    email.requestFocus();
                    return;}
                else if( !email.getEditText().getText().toString().trim().matches(emailPattern)){
                    email.setError("Invalid Email");
                    return;
                }
                else{ email.setError(null);}

                //String
                if (length < 10) {
                    phone.setError("Provide Correct Phone Number");
                    phone.requestFocus();
                    return;}
                else{ phone.setError(null);}
                    if (length2 < 6) {
                        pass.setError("Password length should be at least 6 characters long !! ");
                        pass.requestFocus();
                        return;}

                    else{ pass.setError(null);}


                root = FirebaseDatabase.getInstance();
                reference = root.getReference().child("Users");

                String name = FN.getEditText().getText().toString();
                String emailAdd = email.getEditText().getText().toString();
                String phoneNo = phone.getEditText().getText().toString();
                String password = pass.getEditText().getText().toString();


                HelperClass helperClass = new HelperClass(name, emailAdd, phoneNo, password);
                reference.child(phoneNo).setValue(helperClass);


//                Intent intent = new Intent(getApplicationContext(), otp.class);
//                intent.putExtra("phone", phoneNo);
//                startActivity(intent);



//                progressBar.setVisibility(View.VISIBLE);
//                sign.setVisibility(View.VISIBLE);
//
//                PhoneAuthProvider.getInstance().verifyPhoneNumber("+1" + phone.getEditText().getText().toString(), 30, TimeUnit.SECONDS, SignUp.this,
//                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                progressBar.setVisibility(View.GONE);
//                                sign.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                progressBar.setVisibility(View.GONE);
//                                sign.setVisibility(View.VISIBLE);
//                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        Intent intent = new Intent(SignUp.this, otp.class);
//                intent.putExtra("mobile", phone.getEditText().getText().toString());
//                startActivity(intent);

//
//                String name = FN.getEditText().getText().toString();
//                String em = email.getEditText().getText().toString();
//                String ph = phone.getEditText().getText().toString();
//                String passw = pass.getEditText().getText().toString();
//
//                HelperClass helperClass = new HelperClass();
            }
        });
    }
    }