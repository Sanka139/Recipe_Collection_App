package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    ListView lvRecipes;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        lvRecipes = findViewById(R.id.lvRecipes);
        dbHandler = new DBHandler(this);

        lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
            // Get the name of the recipe clicked (e.g., "Rice")
            String selectedRecipe = (String) parent.getItemAtPosition(position);

            // Create intent to open ViewRecipeActivity
            Intent intent = new Intent(RecipeListActivity.this, ViewRecipeActivity.class);

            // Pass the name so the next screen knows which recipe to load from DB
            intent.putExtra("RECIPE_NAME", selectedRecipe);
            startActivity(intent);
        });
    }

    //This method is used to run every time user return to this screen..
    @Override
    protected void onResume() {
        super.onResume();
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        // 1. Ask the app: "Who is logged in right now?"
        android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = pref.getString("current_user", "");

        // 2. Tell the database to get names ONLY for this user
        // This fixes the error because now you are giving it the "String" it requires!
        List<String> namesList = dbHandler.getAllRecipeNames(loggedInUser);

        // 3. Create and set the adapter just like before
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                namesList
        );
        lvRecipes.setAdapter(adapter);
    }
}