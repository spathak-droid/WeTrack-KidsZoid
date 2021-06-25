package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
                if (FN.getEditText().getText().toString().trim().isEmpty() || email.getEditText().getText().toString().trim().isEmpty() ||
                        phone.getEditText().getText().toString().trim().isEmpty() || pass.getEditText().getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUp.this, "Please Fill All the Boxes", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                sign.setVisibility(View.VISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber("+1" + phone.getEditText().getText().toString(), 30, TimeUnit.SECONDS, SignUp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                sign.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                sign.setVisibility(View.VISIBLE);
                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(SignUp.this, otp.class);
                intent.putExtra("mobile", phone.getEditText().getText().toString());
                startActivity(intent);
//                root = FirebaseDatabase.getInstance();
//                reference = root.getReference("");
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