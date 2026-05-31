package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername: EditText = findViewById(R.id.et_username)
        val etPassword: EditText = findViewById(R.id.et_password)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val ivToggle: ImageView = findViewById(R.id.iv_toggle_password)
        val tvSignup: TextView = findViewById(R.id.tv_signup_link)

        ivToggle.setOnClickListener {
            passwordVisible = !passwordVisible
            etPassword.transformationMethod = if (passwordVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            ivToggle.setImageResource(
                if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
            )
            etPassword.setSelection(etPassword.text.length)
        }

        btnLogin.setOnClickListener {
            val input = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Supabase auth butuh email. Jika input bukan email, cari email dari username
            lifecycleScope.launch {
                try {
                    val emailToUse = if (input.contains("@")) {
                        input
                    } else {
                        // Cari email berdasarkan username
                        val profiles = SupabaseClientProvider.client
                            .postgrest["profiles"]
                            .select { filter { eq("username", input) } }
                            .decodeList<com.example.myapplication.models.Profile>()
                        profiles.firstOrNull()?.email ?: run {
                            Toast.makeText(this@LoginActivity, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }

                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        email = emailToUse
                        this.password = password
                    }

                    val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
                    if (session != null) {
                        val prefs = getSharedPreferences("circlo_prefs", MODE_PRIVATE).edit()
                        prefs.putString("user_id", session.user?.id ?: "")
                        prefs.putString("access_token", session.accessToken)
                        prefs.apply()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}