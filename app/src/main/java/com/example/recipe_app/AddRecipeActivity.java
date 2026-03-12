package com.example.recipe_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddRecipeActivity extends AppCompatActivity {

    EditText etName, etTime, etIngredients, etMethod, etComment;
    Button btnSave, btnAddImage;
    ImageView ivRecipeImage;
    DBHandler dbHandler;

    // පින්තූරයේ පථය (Path) සේව් කරගන්න variable එකක්
    Uri selectedImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbHandler = new DBHandler(this);

        // UI IDs නිවැරදිව ලින්ක් කිරීම (XML එකේ තියෙන IDs වලට ගැලපෙන්න)
        etName = findViewById(R.id.etRecipeName);
        etTime = findViewById(R.id.etRecipeTime);
        etIngredients = findViewById(R.id.etRecipeIngredients);
        etMethod = findViewById(R.id.etRecipeSteps);
        etComment = findViewById(R.id.etRecipeComment);
        btnSave = findViewById(R.id.btnAddRecipe);

        // අලුතින් XML එකට එකතු කරපු IDs
        btnAddImage = findViewById(R.id.btnAddImage);
        ivRecipeImage = findViewById(R.id.ivRecipeImage);

        // 1. පින්තූරයක් තෝරන්න Gallery එකට යාම (Permission චෙක් කිරීම සමඟ)
        btnAddImage.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });

        // 2. Save බොත්තම එබූ විට ක්‍රියාවලිය
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String ing = etIngredients.getText().toString().trim();
            String method = etMethod.getText().toString().trim();
            String comment = etComment.getText().toString().trim();

            // පින්තූරයක් තේරුවා නම් එහි Path එක String එකක් ලෙස ගන්නවා
            // නැත්නම් "no_image" කියලා සේව් කරනවා
            String imagePath = (selectedImageUri != null) ? selectedImageUri.toString() : "no_image";

            // SharedPreferences වලින් ලොග් වුණු යූසර්ව ගන්නවා
            android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
            String loggedInUser = pref.getString("current_user", "");

            // අනිවාර්යයෙන් පිරවිය යුතු කොටු චෙක් කිරීම
            if (name.isEmpty() || ing.isEmpty() || method.isEmpty()) {
                Toast.makeText(this, "Please fill in Name, Ingredients, and Method", Toast.LENGTH_SHORT).show();
            } else {
                // DBHandler එකේ insertRecipe එකට දත්ත 7ම යවනවා
                boolean success = dbHandler.insertRecipe(name, ing, method, time, comment, loggedInUser, imagePath);

                if (success) {
                    Toast.makeText(this, "Recipe Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Dashboard එකට ආපසු යාම
                } else {
                    Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Permission චෙක් කර ගැලරිය විවෘත කිරීම
    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ සඳහා
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
            // පරණ Android Version සඳහා
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission Denied to access Gallery", Toast.LENGTH_SHORT).show();
        }
    }

    // Gallery එකෙන් පින්තූරය තෝරාගෙන ආවම වෙන දේ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // පින්තූරය තෝරාගත් පසු එය ImageView එකට සෙට් කිරීම
            ivRecipeImage.setImageURI(selectedImageUri);
            ivRecipeImage.setAlpha(1.0f); // තෝරපු පින්තූරෙ පැහැදිලිව පෙන්නන්න
        }
    }
}