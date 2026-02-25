package com.example.recipecollectionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);

        DBHelper db = new DBHelper(this);
        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        TextView title = findViewById(R.id.textView5);
        TextView ing = findViewById(R.id.textView6);
        TextView steps = findViewById(R.id.textView7);
        Button btnDelete = findViewById(R.id.button2);
        Button btnBack = findViewById(R.id.button1);

        btnBack.setOnClickListener(v -> {
            finish(); // go to dashboard
        });

        Cursor cursor = db.getRecipeDetails(recipeId);
        if (cursor != null && cursor.moveToFirst()) {
            // Using Column Names is safer than using hardcoded numbers like 2, 3, 4
            title.setText("Title: " + cursor.getString(cursor.getColumnIndexOrThrow("title")));
            ing.setText("Ingredients: " + cursor.getString(cursor.getColumnIndexOrThrow("ingredients")));
            steps.setText("Steps: " + cursor.getString(cursor.getColumnIndexOrThrow("steps")));
            cursor.close();
        }

        btnDelete.setOnClickListener(v -> {
            db.deleteRecipe(recipeId);
            Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}