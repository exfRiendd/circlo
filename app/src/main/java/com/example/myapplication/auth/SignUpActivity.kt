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
import com.example.myapplication.R
import com.example.myapplication.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class SignUpActivity : AppCompatActivity() {

    private var passVisible = false
    private var confirmPassVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val etUsername: EditText = findViewById(R.id.et_username)
        val etEmail: EditText = findViewById(R.id.et_email)
        val etPassword: EditText = findViewById(R.id.et_password)
        val etConfirm: EditText = findViewById(R.id.et_confirm_password)
        val btnSignup: Button = findViewById(R.id.btn_signup)
        val ivTogglePass: ImageView = findViewById(R.id.iv_toggle_pass)
        val ivToggleConfirm: ImageView = findViewById(R.id.iv_toggle_confirm)
        val tvLogin: TextView = findViewById(R.id.tv_login_link)

        ivTogglePass.setOnClickListener {
            passVisible = !passVisible
            etPassword.transformationMethod = if (passVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            ivTogglePass.setImageResource(
                if (passVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
            )
            etPassword.setSelection(etPassword.text.length)
        }

        ivToggleConfirm.setOnClickListener {
            confirmPassVisible = !confirmPassVisible
            etConfirm.transformationMethod = if (confirmPassVisible)
                HideReturnsTransformationMethod.getInstance()
            else
                PasswordTransformationMethod.getInstance()
            ivToggleConfirm.setImageResource(
                if (confirmPassVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
            )
            etConfirm.setSelection(etConfirm.text.length)
        }

        btnSignup.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Step 1: Register ke Supabase Auth
                    SupabaseClientProvider.client.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                        data = buildJsonObject {
                            put("username", username)
                        }
                    }

                    // Step 2: Insert manual ke profiles sebagai fallback
                    try {
                        val userId = SupabaseClientProvider.client
                            .auth.currentSessionOrNull()?.user?.id

                        if (userId != null) {
                            val profileData = JsonObject(
                                mapOf(
                                    "id" to JsonPrimitive(userId),
                                    "email" to JsonPrimitive(email),
                                    "username" to JsonPrimitive(username)
                                )
                            )
                            SupabaseClientProvider.client
                                .postgrest["profiles"]
                                .insert(profileData)
                        }
                    } catch (_: Exception) {
                        // Abaikan jika trigger sudah insert duluan
                    }

                    Toast.makeText(
                        this@SignUpActivity,
                        "Akun berhasil dibuat!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this@SignUpActivity, LoginActivity::class.java)
                    )
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Gagal: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}