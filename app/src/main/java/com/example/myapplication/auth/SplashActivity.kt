package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
            if (session != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginOrSignUpActivity::class.java))
            }
            finish()
        }, 2000)
    }
}