package com.example.kidszoid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static int splash = 5000;
    //variables for animation
    Animation top, bottom;
    ImageView image;
    TextView logo, slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        top = AnimationUtils.loadAnimation(this,R.anim.top_aa);
        bottom = AnimationUtils.loadAnimation(this, R.anim.buttom_aa);
        image = findViewById(R.id.gifImageView);
        logo = findViewById(R.id.textView2);
        slogan = findViewById(R.id.textView);

        image.setAnimation(top);
        logo.setAnimation(bottom);
        slogan.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Dash.class);
                startActivity(intent);
                finish();
            }
        },splash);
    }
}