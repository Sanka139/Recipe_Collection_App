package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    TextView tvTotalCount;
    Button btnLogout;
    CardView cardRecipeList, cardAddRecipe;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHandler = new DBHandler(this);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        cardRecipeList = findViewById(R.id.cardRecipeList);
        cardAddRecipe = findViewById(R.id.cardAddRecipe);
        btnLogout = findViewById(R.id.btnLogout);

        android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = pref.getString("current_user", "");

        // Update the count from Database
        int count = dbHandler.getRecipesCount(loggedInUser);
        tvTotalCount.setText(String.valueOf(count));

        cardRecipeList.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RecipeListActivity.class);
            startActivity(intent); // Intent to your List Activity
        });

        cardAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AddRecipeActivity.class);
                startActivity(intent);
            }
        });

        // --- අලුතින් එකතු කළ Logout ක්‍රියාවලිය ---
        btnLogout.setOnClickListener(v -> {
            // 1. SharedPreferences වල තියෙන User Session එක Clear කිරීම
            android.content.SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Session එකේ තියෙන ඔක්කොම දත්ත මකා දමයි
            editor.apply();

            // 2. Sign In Page එකට යාම (screenshots වල තිබුණ SignInActivity එකට)
            Intent intent = new Intent(DashboardActivity.this, SignInActivity.class);

            // 3. Back stack එක clear කිරීම (මෙහෙම කරාම ආයෙත් Back button එක එබුවට Dashboard එකට එන්න බෑ)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // Dashboard එක close කරනවා
        });
    }

    // Refresh the recipe count whenever you return to this screen
    @Override
    protected void onResume() {
        super.onResume();

        // 1. We have to remember who is logged in again!
        android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = pref.getString("current_user", "");

        // 2. Now we tell the database the name so it can find the right drawer
        int count = dbHandler.getRecipesCount(loggedInUser);

        // 3. Put the number on the screen
        tvTotalCount.setText(String.valueOf(count));
    }
}