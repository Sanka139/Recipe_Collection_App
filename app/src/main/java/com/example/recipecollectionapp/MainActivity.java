package com.example.recipecollectionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Variable declarations at class level fix the "symbol not found" error
    DBHelper db;
    ListView listView;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            // 1. Clear the Session (Crucial for a Logout button)
            SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear(); // Removes user_id
            editor.apply();

            // 2. Define the Destination
            Intent intent = new Intent(this, RegisterActivity.class);

            // 3. Start the Activity (The missing piece in your code)
            startActivity(intent);

            // 4. Close the Dashboard/Detail screen
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        // Session Check
        SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserId = pref.getInt("user_id", -1);

        if (currentUserId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Views
        db = new DBHelper(this);
        listView = findViewById(R.id.listView);
        Button btnAdd = findViewById(R.id.button);

        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", (int)id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRecipes();
    }

    private void displayRecipes() {
        Cursor cursor = db.getUserRecipes(currentUserId);
        String[] from = new String[]{DBHelper.COL_RECIPE_TITLE};
        int[] to = new int[]{R.id.tvRecipeTitle};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.recipe_item, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }
}