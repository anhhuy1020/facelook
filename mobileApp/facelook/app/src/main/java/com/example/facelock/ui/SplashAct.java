package com.example.facelock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facelock.R;

public class SplashAct extends AppCompatActivity {

    Animation app_splash, app_splash_subtitle;
    ImageView app_logo;
    TextView app_subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        app_splash_subtitle = AnimationUtils.loadAnimation(this, R.anim.app_splash_subtitle);

        app_subtitle = findViewById(R.id.app_subtitle);
        app_logo = findViewById(R.id.app_logo);

        app_subtitle.startAnimation(app_splash_subtitle);
        app_logo.startAnimation(app_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent gotoLogin = new Intent(SplashAct.this, LoginActivity.class);
                startActivity(gotoLogin);
                finish();
            }
        }, 2000);
    }

}
