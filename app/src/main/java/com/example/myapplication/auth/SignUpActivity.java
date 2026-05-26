package com.example.myapplication.auth;

import android.content.Intent;
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
import com.example.myapplication.models.ApiResponse;
import com.example.myapplication.models.User;
import com.example.myapplication.network.ApiClient;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private boolean passVisible = false;
    private boolean confirmPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText etUsername = findViewById(R.id.et_username);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        EditText etConfirmPassword = findViewById(R.id.et_confirm_password);
        Button btnSignup = findViewById(R.id.btn_signup);
        ImageView ivTogglePass = findViewById(R.id.iv_toggle_pass);
        ImageView ivToggleConfirm = findViewById(R.id.iv_toggle_confirm);
        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        // Toggle password
        ivTogglePass.setOnClickListener(v -> {
            if (passVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePass.setImageResource(R.drawable.ic_visibility);
                passVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePass.setImageResource(R.drawable.ic_visibility_off);
                passVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Toggle confirm password
        ivToggleConfirm.setOnClickListener(v -> {
            if (confirmPassVisible) {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggleConfirm.setImageResource(R.drawable.ic_visibility);
                confirmPassVisible = false;
            } else {
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggleConfirm.setImageResource(R.drawable.ic_visibility_off);
                confirmPassVisible = true;
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // Tombol Sign Up
        btnSignup.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("email", email);
            body.put("password", password);

            ApiClient.getApiService().register(body).enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call,
                                       Response<ApiResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        Toast.makeText(SignUpActivity.this,
                                "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Gagal membuat akun", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this,
                            "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Link ke Login
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}