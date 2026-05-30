package com.example.myapplication.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("circlo_prefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getInt("user_id", 0) != 0;
            if (isLoggedIn) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, SignUpActivity.class));
            }
            finish();
        }, 2000);
    }
}