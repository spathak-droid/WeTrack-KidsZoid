package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {

    String verificationCodeBySystem;
    private EditText icode1, icode2, icode3, icode4, icode5, icode6;
    Button verify;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otp);

        TextView tm = findViewById(R.id.mobile);
        tm.setText(String.format("+1-%s",getIntent().getStringExtra("phone")));

        verify = findViewById(R.id.sign);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        icode1 = findViewById(R.id.code1);
        icode2 = findViewById(R.id.code2);
        icode3 = findViewById(R.id.code3);
        icode4 = findViewById(R.id.code4);
        icode5 = findViewById(R.id.code5);
        icode6 = findViewById(R.id.code6);

        String Phone = getIntent().getStringExtra("phone");

        sendVerification(Phone);
        setupOTPInputs();




        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code1 = icode1.getText().toString().trim();
                String code2 = icode2.getText().toString().trim();
                String code3 = icode3.getText().toString().trim();
                String code4 = icode4.getText().toString().trim();
                String code5 = icode5.getText().toString().trim();
                String code6 = icode6.getText().toString().trim();
                String code = code1 + code2 + code3 + code4 + code5 + code6;
                if (code.isEmpty() || code.length() < 6){
                    icode1.setError("W");
                    icode2.setError("R");
                    icode3.setError("O");
                    icode4.setError("N");
                    icode5.setError("G");
                    icode6.setError("!");
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);}
            }
        });
    }

    private void sendVerification(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+1" + phone, 15, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String verification){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, verification);
        signInCredential(credential);
    }

    private void signInCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(otp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), UserScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(otp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void setupOTPInputs(){
        icode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    icode2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        icode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    icode3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        icode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    icode4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        icode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    icode5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        icode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    icode6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}