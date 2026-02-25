package com.example.recipecollectionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe_activity); // Your XML name

        // 1. Initialize DB and Views
        DBHelper db = new DBHelper(this);
        EditText etRecipeTitle = findViewById(R.id.etRecipeTitle);
        EditText etIngredients = findViewById(R.id.etIngredients);
        EditText etSteps = findViewById(R.id.etSteps);
        Button btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        Button btnBack = findViewById(R.id.button1);

        btnBack.setOnClickListener(v -> {
            finish(); // go to dashboard
        });

        // 2. Save Button Logic
        btnSaveRecipe.setOnClickListener(v -> {
            String title = etRecipeTitle.getText().toString().trim();
            String ing = etIngredients.getText().toString().trim();
            String steps = etSteps.getText().toString().trim();

            if (title.isEmpty() || ing.isEmpty() || steps.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve the logged-in user's ID from SharedPreferences
            SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
            int currentUserId = pref.getInt("user_id", -1);

            // 3. Save to Database
            boolean success = db.addRecipe(currentUserId, title, ing, steps);

            if (success) {
                Toast.makeText(this, "Recipe Added Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Goes back to MainActivity
            } else {
                Toast.makeText(this, "Error saving recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }
}