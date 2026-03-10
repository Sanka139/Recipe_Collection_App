package com.example.recipe_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRecipeActivity extends AppCompatActivity {

    EditText etName, etTime, etIngredients, etMethod, etComment;
    Button btnSave;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbHandler = new DBHandler(this);

        etName = findViewById(R.id.etName);
        etTime = findViewById(R.id.etTime);
        etIngredients = findViewById(R.id.etIngredients);
        etMethod = findViewById(R.id.etMethod);
        etComment = findViewById(R.id.etComment);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String ing = etIngredients.getText().toString().trim();
            String method = etMethod.getText().toString().trim();
            String comment = etComment.getText().toString().trim();

            // 1. Get the "Name Tag" (User Name)
            android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
            String loggedInUser = pref.getString("current_user", "");

            // 2. Check if the boxes are empty
            if (name.isEmpty() || ing.isEmpty() || method.isEmpty()) {
                Toast.makeText(this, "Please fill in Name, Ingredients, and Method", Toast.LENGTH_SHORT).show();
            } else {
                // 3. Save it! (Make sure to include 'loggedInUser' at the end)
                boolean success = dbHandler.insertRecipe(name, ing, method, time, comment, loggedInUser);

                if (success) {
                    Toast.makeText(this, "Recipe Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the dashboard
                } else {
                    Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}