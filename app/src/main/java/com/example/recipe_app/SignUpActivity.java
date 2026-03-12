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

    EditText username, emailField, password, confirmPassword;
    Button btnRegister;
    TextView tvLogin;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        username = findViewById(R.id.etRegUsername);
        emailField = findViewById(R.id.etRegEmail);
        password = findViewById(R.id.etcreatePassword);
        confirmPassword = findViewById(R.id.etconfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvBackToLogin);
        dbHandler = new DBHandler(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String confPass = confirmPassword.getText().toString().trim();


                if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || confPass.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

                else if (!pass.equals(confPass)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }

                else {
                    boolean isInserted = dbHandler.insertUser(user, email, pass);
                    if (isInserted) {
                        Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Register වුණාම ආපහු Login එකටම යනවා
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}