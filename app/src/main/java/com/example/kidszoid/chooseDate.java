package com.example.kidszoid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class chooseDate extends AppCompatActivity {
    TextInputEditText select_date ;
    boolean doublePress;
    Button ok;
    String date, school;
    Dialog dialog;
    TextInputLayout layout;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<Person> pickup_array;
    ArrayList<Person> dropoff_array;
    AutoCompleteTextView autoCompleteTextView;
    DatePickerDialog.OnDateSetListener setListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_date);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        select_date = findViewById(R.id.et_date);
        ok = findViewById(R.id.button_ok_date);

        school = getIntent().getStringExtra("school");

        layout =(TextInputLayout)findViewById(R.id.et_drop_pick);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);

        arrayList = new ArrayList<>();
        arrayList.add("Pick-Up");
        arrayList.add("Drop-off");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdownitem, arrayList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setThreshold(1);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(chooseDate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        if ((day / 10) < 1 &&  (month /10) <1){
                            date = "0"+month+"-"+"0"+day+"-"+year;
                        }else if ((day / 10) < 1){
                            date = month+"-"+"0"+day+"-"+year;
                        }else if ((month /10) < 1){
                            date = "0"+month+"-"+day+"-"+year;
                        }else{
                            date = month+"-"+day+"-"+year;
                        }

                        select_date.setText(date);


                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropoff_array = new ArrayList<Person>();
                pickup_array = new ArrayList<Person>();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("History").child(school).child(select_date.getText().toString());
                DatabaseReference p = databaseReference.child("pickup");
                String pickOrdrop = layout.getEditText().getText().toString().trim();
                DatabaseReference p1 = databaseReference.child("dropoff");
                p.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot bb : snapshot.getChildren()) {
                                Log.d("name", "onDataChange: " + bb.getKey().toUpperCase());
                                Log.d("time", "onDataChange: " + bb.getValue().toString());
                                Person aa = new Person(bb.getKey(), bb.getValue().toString());
                                pickup_array.add(aa);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

                p1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot bb : snapshot.getChildren()) {
                                Log.d("name", "onDataChange: " + bb.getKey().toUpperCase());
                                Log.d("time", "onDataChange: " + bb.getValue().toString());
                                Person aa = new Person(bb.getKey(), bb.getValue().toString());
                                dropoff_array.add(aa);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});
                Log.d("chaman", "onClick: " + pickup_array);

                if (doublePress){
                if (pickOrdrop.equals("Pick-Up")){

                    dialog = new Dialog(chooseDate.this);
                    dialog.setContentView(R.layout.showhistory);

                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.showlisc));
                    dialog.setCancelable(true);
                    TextView t = dialog.findViewById(R.id.insert_date);
                    ListView l = dialog.findViewById(R.id.listview3);
                    PersonListAdapter adapter = new PersonListAdapter(chooseDate.this, R.layout.adapter_view, pickup_array);
                    l.setAdapter(adapter);
                    t.setText(date);
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();

                }else if (pickOrdrop.equals("Drop-off")){

                    dialog = new Dialog(chooseDate.this);
                    dialog.setContentView(R.layout.showhistory);

                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.showlisc));
                    dialog.setCancelable(true);
                    TextView t = dialog.findViewById(R.id.insert_date);
                    ListView l = dialog.findViewById(R.id.listview3);
                    PersonListAdapter adapter = new PersonListAdapter(chooseDate.this, R.layout.adapter_view, dropoff_array);
                    l.setAdapter(adapter);
                    t.setText(date);
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();


                }
            }else{
                Toast.makeText(chooseDate.this, "Double click to view history", Toast.LENGTH_SHORT).show();
                doublePress= true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doublePress = false;
                    }
                }, 2000);
            }
            }
        });



    }
}