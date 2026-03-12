package com.example.recipe_app;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditRecipeActivity extends AppCompatActivity {

    EditText etName, etTime, etIngredients, etMethod, etComment;
    Button btnSave;
    DBHandler dbHandler;
    String recipeNameToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using the same layout as Add Recipe since they are visually identical
        setContentView(R.layout.activity_add_recipe);

        dbHandler = new DBHandler(this);

        // Get the name passed from the View Recipe screen
        recipeNameToEdit = getIntent().getStringExtra("RECIPE_NAME");

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etTime = findViewById(R.id.etTime);
        etIngredients = findViewById(R.id.etIngredients);
        etMethod = findViewById(R.id.etMethod);
        etComment = findViewById(R.id.etComment);
        btnSave = findViewById(R.id.btnSave);

        // Change button text to "Save Changes" or "Update"
        btnSave.setText("Save");

        // Pre-fill the fields with current database data
        loadCurrentData();

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();
            String newIng = etIngredients.getText().toString().trim();
            String newMethod = etMethod.getText().toString().trim();
            String newComment = etComment.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Call update method in DBHandler
                boolean isUpdated = dbHandler.updateRecipe(recipeNameToEdit, newName, newIng, newMethod, newTime, newComment);

                if (isUpdated) {
                    Toast.makeText(this, "Recipe Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to previous screen
                } else {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCurrentData() {
        Cursor cursor = dbHandler.getRecipeDetails(recipeNameToEdit);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(1));        // Recipe name
            etIngredients.setText(cursor.getString(2)); // Ingredients
            etMethod.setText(cursor.getString(3));      // Methods
            etTime.setText(cursor.getString(4));        // Time
            etComment.setText(cursor.getString(5));     // Comment
            cursor.close();
        }
    }
}