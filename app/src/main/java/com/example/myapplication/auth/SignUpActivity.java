package com.example.circlo.auth;

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
import com.example.circlo.R;
import com.example.circlo.main.MainActivity;

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

            // TODO: Ganti dengan registrasi nyata (Firebase / API)
            Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });

        // Link ke Login
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
