package com.example.kidszoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class SchoolDropDown extends AppCompatActivity {

    TextInputLayout layout;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;

    public  static final String fileName = "login";
    public  static final String Username = "username";



    Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_drop_down);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        ok = findViewById(R.id.button_ok);
        layout =(TextInputLayout)findViewById(R.id.layout);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        arrayList = new ArrayList<>();
        arrayList.add("Arlington High School");
        arrayList.add("Arlington Collegiate High School");
        arrayList.add("Lamar High School");
        arrayList.add("James Martin High School");
        arrayList.add("Premier High School");
        arrayList.add("James Bowie High School");
        arrayList.add("Arlington College and Carrer High School");
        arrayList.add("Sam Houston High School");
        arrayList.add("Juan Seguin High School");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdownitem, arrayList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setThreshold(1);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SchoolScreen.class);
                String school = layout.getEditText().getText().toString().trim();
                if(!school.equals("Please Select Your School")){
                    intent.putExtra("school", school);
                    intent.putExtra("email", email);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Make a Selection",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}