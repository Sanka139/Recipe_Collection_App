package com.example.recipe_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewRecipeActivity extends AppCompatActivity {

    TextView tvName, tvSteps, tvTime, tvIngredients, tvComment;
    Button btnEdit, btnDelete;
    DBHandler dbHandler;
    String currentRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        dbHandler = new DBHandler(this);
        currentRecipeName = getIntent().getStringExtra("RECIPE_NAME");

        // Initialize Views
        tvName = findViewById(R.id.tvViewName);
        tvSteps = findViewById(R.id.tvViewSteps);
        tvTime = findViewById(R.id.tvViewTime);
        tvIngredients = findViewById(R.id.tvViewIngredients);
        tvComment = findViewById(R.id.tvViewComment);

        // INITIALIZE BOTH BUTTONS HERE
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit); // You were missing this line!

        if (currentRecipeName != null) {
            loadRecipeData();
        } else {
            Toast.makeText(this, "Error: Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnDelete.setOnClickListener(v -> {
            dbHandler.deleteRecipe(currentRecipeName);
            Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewRecipeActivity.this, EditRecipeActivity.class);
            intent.putExtra("RECIPE_NAME", currentRecipeName);
            startActivity(intent);
        });
    }

    // This method ensures the data refreshes when returning from the Edit screen
    @Override
    protected void onResume() {
        super.onResume();
        if (currentRecipeName != null) {
            loadRecipeData();
        }
    }

    private void loadRecipeData() {
        Cursor cursor = dbHandler.getRecipeDetails(currentRecipeName);
        if (cursor != null && cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow("recipe_name")));
            tvIngredients.setText(cursor.getString(cursor.getColumnIndexOrThrow("ingredients")));
            tvSteps.setText(cursor.getString(cursor.getColumnIndexOrThrow("instructions")));
            tvTime.setText(cursor.getString(cursor.getColumnIndexOrThrow("total_time")));
            tvComment.setText(cursor.getString(cursor.getColumnIndexOrThrow("user_comment")));
            cursor.close();
        }
    }
}