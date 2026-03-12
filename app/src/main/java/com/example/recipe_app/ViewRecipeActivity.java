package com.example.recipe_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewRecipeActivity extends AppCompatActivity {

    TextView tvName, tvSteps, tvTime, tvIngredients, tvComment;
    ImageView ivRecipe;
    Button btnEdit, btnDelete;
    DBHandler dbHandler;
    String currentRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        dbHandler = new DBHandler(this);
        currentRecipeName = getIntent().getStringExtra("RECIPE_NAME");

        ivRecipe = findViewById(R.id.ivViewRecipeImage);
        tvName = findViewById(R.id.tvViewName);
        tvSteps = findViewById(R.id.tvViewSteps);
        tvTime = findViewById(R.id.tvViewTime);
        tvIngredients = findViewById(R.id.tvViewIngredients);
        tvComment = findViewById(R.id.tvViewComment);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);

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

            // පින්තූරය පෙන්වන කොටස
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            if (imagePath != null && !imagePath.equals("no_image")) {
                ivRecipe.setImageURI(Uri.parse(imagePath));
            } else {
                ivRecipe.setImageResource(R.drawable.img3); // පින්තූරයක් නැතිනම් Default එකක්
            }
            cursor.close();
        }
    }
}