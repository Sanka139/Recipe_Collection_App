package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText username, password;
    Button btnRegister;
    TextView tvLogin;
    DBHandler dbHandler;

    EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.etRegUsername);
        password = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvBackToLogin);
        dbHandler = new DBHandler(this);
        emailField = findViewById(R.id.etRegEmail);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String email = emailField.getText().toString(); // Get Email
                String pass = password.getText().toString();

                if (user.equals("") || email.equals("") || pass.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Pass all three values to the DBHandler
                    boolean isInserted = dbHandler.insertUser(user, email, pass);
                    if (isInserted) {
                        Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        // Link back to Login Screen
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}