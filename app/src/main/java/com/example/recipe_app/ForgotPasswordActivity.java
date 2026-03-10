package com.example.recipe_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etIdentity, etNewPass, etConfirmPass;
    Button btnConfirm;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etIdentity = findViewById(R.id.etResetIdentity);
        etNewPass = findViewById(R.id.etNewPassword);
        etConfirmPass = findViewById(R.id.etConfirmNewPassword);
        btnConfirm = findViewById(R.id.btnConfirmReset);
        dbHandler = new DBHandler(this);

        btnConfirm.setOnClickListener(v -> {
            String identity = etIdentity.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            if (identity.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else {
                // Execute update
                boolean isUpdated = dbHandler.updatePassword(identity, newPass);
                if (isUpdated) {
                    Toast.makeText(this, "Password Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Login
                } else {
                    Toast.makeText(this, "User/Email not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}