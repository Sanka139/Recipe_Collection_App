package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    EditText username, password;
    Button btnSignIn;
    DBHandler dbHandler; // This refers to your SQLite Helper class from Lab 09
    TextView tvGoToSignUp;
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        dbHandler = new DBHandler(this);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputIdentity = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // 1. First, check if the boxes are empty
                if (inputIdentity.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop here if empty
                }

                // 2. Ask the DB for the REAL username (returns null if login fails)
                String confirmedUsername = dbHandler.checkUserLogin(inputIdentity, pass);

                // 3. If we got a name back, the login is successful!
                if (confirmedUsername != null) {
                    // ALWAYS save the real username so they see their recipes
                    getSharedPreferences("UserSession", MODE_PRIVATE)
                            .edit()
                            .putString("current_user", confirmedUsername)
                            .apply();

                    Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    // 4. If confirmedUsername is null, the login failed
                    Toast.makeText(SignInActivity.this, "Error: Incorrect Password, Email, or Username", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 3. Link the "Sign Up" text to the SignUpActivity
        tvGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgotPasswordActivity
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}