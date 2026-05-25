package com.example.myapplication.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.MainActivity;
import com.example.myapplication.models.ApiResponse;   // ✅ models (pakai S)
import com.example.myapplication.models.User;           // ✅ models (pakai S)
import com.example.myapplication.network.ApiClient;     // ✅ network

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        ImageView ivTogglePassword = findViewById(R.id.iv_toggle_password);
        TextView tvSignupLink = findViewById(R.id.tv_signup_link);

        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
                passwordVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                passwordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            ApiClient.getApiService().login(body).enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call,
                                       Response<ApiResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        User user = response.body().getUser();

                        SharedPreferences.Editor editor = getSharedPreferences("circlo_prefs", MODE_PRIVATE).edit();
                        editor.putInt("user_id", user.getId());
                        editor.putString("username", user.getUsername());
                        editor.putString("email", user.getEmail());
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,
                            "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignupLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}