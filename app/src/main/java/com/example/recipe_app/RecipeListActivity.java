package com.example.recipe_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity {

    ListView lvRecipes;
    DBHandler dbHandler;
    ArrayList<RecipeModel> recipeList;
    RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        lvRecipes = findViewById(R.id.lvRecipes);
        dbHandler = new DBHandler(this);

        lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRecipe = recipeList.get(position).getName();

            Intent intent = new Intent(RecipeListActivity.this, ViewRecipeActivity.class);
            intent.putExtra("RECIPE_NAME", selectedRecipe);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecipeList();
    }

    private void refreshRecipeList() {
        android.content.SharedPreferences pref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = pref.getString("current_user", "");

        recipeList = new ArrayList<>();
        Cursor cursor = dbHandler.getAllRecipesCursor(loggedInUser);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Database column names හරියටම ගැලපෙන්න ඕනේ
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("recipe_name"));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("total_time"));

                    recipeList.add(new RecipeModel(name, imagePath, time));
                } while (cursor.moveToNext());
            }
            cursor.close(); // Cursor එක අනිවාර්යයෙන්ම close කිරීම
        }

        adapter = new RecipeAdapter(this, recipeList);
        lvRecipes.setAdapter(adapter);
    }

    class RecipeModel {
        String name, imagePath, time;
        RecipeModel(String name, String imagePath, String time) {
            this.name = name;
            this.imagePath = imagePath;
            this.time = time;
        }
        public String getName() { return name; }
        public String getImagePath() { return imagePath; }
        public String getTime() { return time; }
    }

    class RecipeAdapter extends BaseAdapter {
        Context context;
        ArrayList<RecipeModel> items;

        RecipeAdapter(Context context, ArrayList<RecipeModel> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() { return items.size(); }
        @Override
        public Object getItem(int i) { return items.get(i); }
        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, viewGroup, false);
            }

            ImageView ivRecipe = view.findViewById(R.id.ivRowImage);
            TextView tvName = view.findViewById(R.id.tvRowRecipeName);
            TextView tvTime = view.findViewById(R.id.tvRowTime);

            RecipeModel currentItem = items.get(i);

            tvName.setText(currentItem.getName());
            tvTime.setText(currentItem.getTime());

            // Image load කිරීමේදී crash වීම වැලැක්වීමට try-catch භාවිතය
            if (currentItem.getImagePath() != null && !currentItem.getImagePath().equals("no_image")) {
                try {
                    ivRecipe.setImageURI(Uri.parse(currentItem.getImagePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    ivRecipe.setImageResource(R.drawable.img3); // Error එකක් ආවොත් default image එකක් පෙන්වන්න
                }
            } else {
                ivRecipe.setImageResource(R.drawable.img3);
            }

            return view;
        }
    }
}