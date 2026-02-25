package com.example.recipecollectionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this matches your login layout name

        // 1. Initialize Views (Lab 05)
        EditText etEmail = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.textView3);
        DBHelper db = new DBHelper(this);

        // GoToRegister
        tvGoToRegister.setOnClickListener(v -> {
            // Navigate from Login to Register
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 2. Handle Login Logic
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String pass = etPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Hash input to compare with stored hash in DB
                String securePass = hashPassword(pass);
                int userId = db.checkUser(email, securePass);

                if (userId != -1) {
                    // SUCCESS: Create Session (Mandatory Requirement)
                    SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("user_id", userId);
                    editor.apply();

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to Home (Lab 07)
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close login so user can't go back
                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Password Hashing Method (Mandatory Security)
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
}