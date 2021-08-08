package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {

    String verificationCodeBySystem;
    TextView resend;
    String Phone, email, name, password;
    private EditText icode1, icode2, icode3, icode4, icode5, icode6;
    Button verify;
    ProgressBar progressBar;
    int code;

    FirebaseDatabase root;
    DatabaseReference reference;

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
        resend = findViewById(R.id.resend);

        icode1 = findViewById(R.id.code1);
        icode2 = findViewById(R.id.code2);
        icode3 = findViewById(R.id.code3);
        icode4 = findViewById(R.id.code4);
        icode5 = findViewById(R.id.code5);
        icode6 = findViewById(R.id.code6);

        Phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference().child("Users");
//        HelperClass helperClass = new HelperClass(name, email, Phone, password);
//        reference.child(Phone).setValue(helperClass);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(otp.this, "Code sent Again!!", Toast.LENGTH_SHORT).show();
                sendVerification(Phone);
                setupOTPInputs();
            }
        });




      sendVerification(Phone);
        //sendEmail();
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
                String input = code1 + code2 + code3 + code4 + code5 + code6;
                if (input.isEmpty() || input.length() < 6){
                    Toast.makeText(otp.this,"This Field is Required for Verification", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);
//                    }else{
//                        Toast.makeText(otp.this,"Failed! Please Enter the correct code sent to your email",Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
    }

    private void sendVerification(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+1" + phone, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
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
            progressBar.setVisibility(View.INVISIBLE);
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
                    HelperClass helperClass = new HelperClass(name, email, Phone, password);
                    reference.child(Phone).setValue(helperClass);

                    Intent intent = new Intent(otp.this, Dash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }else{
                    Toast.makeText(otp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

//    public void sendEmail(){
//        Random random = new Random();
//        code = random.nextInt(899999)+100000;
//        String url = "http://sandesh1543.000webhostapp.com/html.php";
////        WebSettings webSettings = web.getSettings();
////        webSettings.setJavaScriptEnabled(true);
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(otp.this, response,Toast.LENGTH_SHORT).show();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(otp.this, error.getMessage(),Toast.LENGTH_SHORT).show();
//
//            }
//        }){
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<>();
//                String email = getIntent().getStringExtra("email");
//                params.put("email",email);
//                params.put("code",String.valueOf(code));
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
//
//
//
//    }

//    public void checkCode(View view){
//        String input = icode1.getText().toString()+icode2.getText().toString()+icode3.getText().toString()+icode4.getText().toString()+
//                icode5.getText().toString()+icode6.getText().toString();
//        if(input.equals(String.valueOf(code))){
//            Toast.makeText(otp.this,"Successfully verified",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(otp.this,"Failed! Please Enter the correct code sent to your email",Toast.LENGTH_SHORT).show();
//        }
//    }


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