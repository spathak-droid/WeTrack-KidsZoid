package com.example.kidszoid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class kids extends AppCompatActivity {
     private EditText name1,grade1,make,model,number;
    TextInputLayout phone1;
    private Button btn1,add_kid,add2,add_vehicle,another_vehicle;
    String parentsname;
    ImageView back;

    private DatabaseReference reference;
    private DatabaseReference kidreference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent =getIntent();
        parentsname =intent.getStringExtra("name");

        mAuth = FirebaseAuth.getInstance();
        reference =FirebaseDatabase.getInstance().getReference("Kids");
        DatabaseReference referencename =reference.child(parentsname);
       // DatabaseReference vehicle_ref   =reference.child("Vehicle");

        back =findViewById(R.id.back);
        name1=findViewById(R.id.name1);
        make=findViewById(R.id.make);
        model=findViewById(R.id.model);
        number=findViewById(R.id.number);

        grade1=findViewById(R.id.grade1);


        btn1= findViewById(R.id.btn1);
        another_vehicle= findViewById(R.id.another_vehicle);


        add_kid=findViewById(R.id.add_kid);
        add2 =findViewById(R.id.add2);
        add_vehicle = findViewById(R.id.add_vehicle);





        add2.setVisibility(View.GONE);

        another_vehicle.setVisibility(View.GONE);
        make.setVisibility(View.GONE);
        model.setVisibility(View.GONE);
        number.setVisibility(View.GONE);

        add_kid.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (name1.getText().toString().trim().isEmpty()) {
                    name1.setError(" Field Cannot be empty");
                    name1.requestFocus();
                    return;}
                else if (grade1.getText().toString().trim().isEmpty()) {
                    grade1.setError(" Field Cannot be empty");
                    grade1.requestFocus();
                    return;}
                 else {
                     DatabaseReference kid=referencename.child("Kids");
                    String firstkid = name1.getText().toString();
                    DatabaseReference childRef = kid.child(firstkid);
                    childRef.child("Name").setValue(firstkid);

                    String firstgrade = grade1.getText().toString();
                    // DatabaseReference childRef2 =reference.child("First Kid's Grade:");
                    childRef.child("Grade:").setValue(firstgrade);
                }
            }});

        //Add another kid
        btn1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                name1.getText().clear();
                grade1.getText().clear();

            }});

        add_vehicle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                make.setVisibility(View.VISIBLE);
                model.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);
                add2.setVisibility(View.VISIBLE);
                another_vehicle.setVisibility(View.VISIBLE);
            }

            });
        another_vehicle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                make.getText().clear();
                model.getText().clear();
                number.getText().clear();


            }});



        add2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (make.getText().toString().trim().isEmpty()) {
                    make.setError(" Field Cannot be empty");
                    make.requestFocus();
                    return;}
                else if (model.getText().toString().trim().isEmpty()) {
                    model.setError(" Field Cannot be empty");
                    model.requestFocus();
                    return;}
                else if (number.getText().toString().trim().isEmpty()) {
                    number.setError(" Field Cannot be empty");
                    number.requestFocus();
                    return;}
                else {
                    DatabaseReference vehicle_ref = referencename.child("Vehicles");
                    String make1 = make.getText().toString();
                    DatabaseReference childRef_make = vehicle_ref.child(make1);
                    childRef_make.child("Make").setValue(make1);

                    String model1 = model.getText().toString();
                    childRef_make.child("Model").setValue(model1);

                    String plate = number.getText().toString();
                    childRef_make.child("License Plate number").setValue(plate);



                }


            }
        });





        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(kids.this, UserScreen.class);
                startActivity(intent);
            }
            });
    }
}