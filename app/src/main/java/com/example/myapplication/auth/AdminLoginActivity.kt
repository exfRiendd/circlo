package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.admin.AdminDashboardActivity
import com.example.myapplication.models.Profile
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class AdminLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val etEmail: EditText = findViewById(R.id.et_admin_email)
        val etPassword: EditText = findViewById(R.id.et_admin_password)
        val btnLogin: Button = findViewById(R.id.btn_admin_login)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }

                    val userId = SupabaseClientProvider.client
                        .auth.currentSessionOrNull()?.user?.id ?: return@launch

                    val profiles = SupabaseClientProvider.client
                        .postgrest["profiles"]
                        .select()
                        .decodeList<Profile>()

                    val profile = profiles.firstOrNull { it.id == userId }

                    if (profile?.role == "admin") {
                        startActivity(Intent(this@AdminLoginActivity,
                            AdminDashboardActivity::class.java))
                        finish()
                    } else {
                        SupabaseClientProvider.client.auth.signOut()
                        Toast.makeText(this@AdminLoginActivity,
                            "Akun bukan admin", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AdminLoginActivity,
                        "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}