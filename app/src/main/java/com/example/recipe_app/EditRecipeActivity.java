package com.example.recipe_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditRecipeActivity extends AppCompatActivity {

    EditText etName, etTime, etIngredients, etMethod, etComment;
    Button btnSave, btnChangeImage;
    ImageView ivRecipeImage;
    DBHandler dbHandler;
    String recipeNameToEdit;

    // දැනට තියෙන පින්තූරේ හෝ අලුතෙන් තෝරන පින්තූරේ URI එක තියාගන්න
    Uri selectedImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add Recipe එකේ Layout එකම පාවිච්චි කරනවා
        setContentView(R.layout.activity_add_recipe);

        dbHandler = new DBHandler(this);
        recipeNameToEdit = getIntent().getStringExtra("RECIPE_NAME");

        // XML එකේ තියෙන අලුත් IDs වලට ගැලපෙන්න UI Components Initialize කිරීම
        etName = findViewById(R.id.etRecipeName);
        etTime = findViewById(R.id.etRecipeTime);
        etIngredients = findViewById(R.id.etRecipeIngredients);
        etMethod = findViewById(R.id.etRecipeSteps);
        etComment = findViewById(R.id.etRecipeComment);
        btnSave = findViewById(R.id.btnAddRecipe);
        btnChangeImage = findViewById(R.id.btnAddImage);
        ivRecipeImage = findViewById(R.id.ivRecipeImage);

        // බොත්තමේ නම වෙනස් කරනවා Update එකට ගැලපෙන්න
        btnSave.setText("UPDATE RECIPE");
        btnChangeImage.setText("CHANGE PHOTO");

        // දැනට තියෙන දත්ත පිරවීම (පින්තූරයත් සමඟ)
        loadCurrentData();

        // පින්තූරය වෙනස් කිරීමේ බොත්තම
        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();
            String newIng = etIngredients.getText().toString().trim();
            String newMethod = etMethod.getText().toString().trim();
            String newComment = etComment.getText().toString().trim();

            // පින්තූරයක් තෝරලා තියෙනවා නම් ඒක ගන්නවා, නැත්නම් "no_image" කියලා යවනවා
            String imagePath = (selectedImageUri != null) ? selectedImageUri.toString() : "no_image";

            if (newName.isEmpty()) {
                Toast.makeText(this, "Recipe name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // DBHandler එකේ updateRecipe මෙතඩ් එකට දත්ත යවනවා
                boolean isUpdated = dbHandler.updateRecipe(recipeNameToEdit, newName, newIng, newMethod, newTime, newComment, imagePath);

                if (isUpdated) {
                    Toast.makeText(this, "Recipe Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCurrentData() {
        Cursor cursor = dbHandler.getRecipeDetails(recipeNameToEdit);
        if (cursor != null && cursor.moveToFirst()) {
            // DBHandler එකේ තියෙන column names නිවැරදිද කියලා චෙක් කරගන්න
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("recipe_name")));
            etIngredients.setText(cursor.getString(cursor.getColumnIndexOrThrow("ingredients")));
            etMethod.setText(cursor.getString(cursor.getColumnIndexOrThrow("instructions")));
            etTime.setText(cursor.getString(cursor.getColumnIndexOrThrow("total_time")));
            etComment.setText(cursor.getString(cursor.getColumnIndexOrThrow("user_comment")));

            // පින්තූරය පෙන්වීම
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            if (imagePath != null && !imagePath.equals("no_image")) {
                selectedImageUri = Uri.parse(imagePath);
                ivRecipeImage.setImageURI(selectedImageUri);
            }
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivRecipeImage.setImageURI(selectedImageUri);
        }
    }
}