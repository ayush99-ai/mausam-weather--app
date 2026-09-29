package com.example.farmers;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmers.util.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnModeLogin;
    private MaterialButton btnModeSignUp;
    private MaterialButton btnTabEmail;
    private MaterialButton btnTabPhone;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private Button btnLogin;
    private TextView tvToggleMode;

    private boolean isSignUpMode = false;
    private boolean isEmailMethod = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnModeLogin = findViewById(R.id.btnModeLogin);
        btnModeSignUp = findViewById(R.id.btnModeSignUp);
        btnTabEmail = findViewById(R.id.btnTabEmail);
        btnTabPhone = findViewById(R.id.btnTabPhone);

        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvToggleMode = findViewById(R.id.tvToggleMode);

        PrefsManager prefs = PrefsManager.getInstance(this);

        // Pre-fill existing credentials if present
        if (prefs.getEmail() != null && !prefs.getEmail().isEmpty()) {
            etEmail.setText(prefs.getEmail());
        }
        if (prefs.getPhone() != null && !prefs.getPhone().isEmpty()) {
            etPhone.setText(prefs.getPhone());
        }

        switchToLoginMode();

        if ("phone".equalsIgnoreCase(prefs.getLoginMethod())) {
            switchToPhoneMethod();
        } else {
            switchToEmailMethod();
        }

        btnModeLogin.setOnClickListener(v -> switchToLoginMode());
        btnModeSignUp.setOnClickListener(v -> switchToSignUpMode());

        btnTabEmail.setOnClickListener(v -> switchToEmailMethod());
        btnTabPhone.setOnClickListener(v -> switchToPhoneMethod());

        tvToggleMode.setOnClickListener(v -> {
            if (isSignUpMode) {
                switchToLoginMode();
            } else {
                switchToSignUpMode();
            }
        });

        btnLogin.setOnClickListener(v -> handleAuth(prefs));
    }

    private void switchToLoginMode() {
        isSignUpMode = false;
        int activeBg = ContextCompat.getColor(this, R.color.primary_green);
        int secondaryTextColor = ContextCompat.getColor(this, R.color.text_secondary);

        btnModeLogin.setBackgroundColor(activeBg);
        btnModeLogin.setTextColor(Color.WHITE);

        btnModeSignUp.setBackgroundColor(Color.TRANSPARENT);
        btnModeSignUp.setTextColor(secondaryTextColor);

        tilConfirmPassword.setVisibility(View.GONE);
        btnLogin.setText(R.string.login_mode);
        tvToggleMode.setText(R.string.dont_have_account);
    }

    private void switchToSignUpMode() {
        isSignUpMode = true;
        int activeBg = ContextCompat.getColor(this, R.color.primary_green);
        int secondaryTextColor = ContextCompat.getColor(this, R.color.text_secondary);

        btnModeSignUp.setBackgroundColor(activeBg);
        btnModeSignUp.setTextColor(Color.WHITE);

        btnModeLogin.setBackgroundColor(Color.TRANSPARENT);
        btnModeLogin.setTextColor(secondaryTextColor);

        tilConfirmPassword.setVisibility(View.VISIBLE);
        btnLogin.setText(R.string.signup_mode);
        tvToggleMode.setText(R.string.already_have_account);
    }

    private void switchToEmailMethod() {
        isEmailMethod = true;
        tilEmail.setVisibility(View.VISIBLE);
        tilPhone.setVisibility(View.GONE);

        int activeBg = ContextCompat.getColor(this, R.color.primary_green);
        int secondaryTextColor = ContextCompat.getColor(this, R.color.text_secondary);

        btnTabEmail.setBackgroundColor(activeBg);
        btnTabEmail.setTextColor(Color.WHITE);
        btnTabEmail.setIconTint(ColorStateList.valueOf(Color.WHITE));

        btnTabPhone.setBackgroundColor(Color.TRANSPARENT);
        btnTabPhone.setTextColor(secondaryTextColor);
        btnTabPhone.setIconTint(ColorStateList.valueOf(secondaryTextColor));
    }

    private void switchToPhoneMethod() {
        isEmailMethod = false;
        tilEmail.setVisibility(View.GONE);
        tilPhone.setVisibility(View.VISIBLE);

        int activeBg = ContextCompat.getColor(this, R.color.primary_green);
        int secondaryTextColor = ContextCompat.getColor(this, R.color.text_secondary);

        btnTabEmail.setBackgroundColor(Color.TRANSPARENT);
        btnTabEmail.setTextColor(secondaryTextColor);
        btnTabEmail.setIconTint(ColorStateList.valueOf(secondaryTextColor));

        btnTabPhone.setBackgroundColor(activeBg);
        btnTabPhone.setTextColor(Color.WHITE);
        btnTabPhone.setIconTint(ColorStateList.valueOf(Color.WHITE));
    }

    private void handleAuth(PrefsManager prefs) {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (isEmailMethod) {
            if (email.isEmpty()) {
                tilEmail.setError("Please enter your email address");
                etEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
                return;
            }
            tilEmail.setError(null);
        } else {
            if (phone.isEmpty()) {
                tilPhone.setError("Please enter your phone number");
                etPhone.requestFocus();
                return;
            }
            if (phone.length() < 8) {
                tilPhone.setError("Please enter a valid phone number");
                etPhone.requestFocus();
                return;
            }
            tilPhone.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Please enter password");
            etPassword.requestFocus();
            return;
        }
        tilPassword.setError(null);

        if (isSignUpMode) {
            if (confirmPassword.isEmpty()) {
                tilConfirmPassword.setError("Please confirm your password");
                etConfirmPassword.requestFocus();
                return;
            }
            if (!password.equals(confirmPassword)) {
                tilConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }
            tilConfirmPassword.setError(null);
        }

        // Save authentication preferences
        prefs.setEmail(email);
        prefs.setPhone(phone);
        prefs.setLoginMethod(isEmailMethod ? "email" : "phone");
        prefs.setLoggedIn(true);
        prefs.setOnboarded(true);

        String successMsg = isSignUpMode ? "Account created successfully!" : "Welcome back!";
        Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
