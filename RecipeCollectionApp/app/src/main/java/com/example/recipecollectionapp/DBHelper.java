package com.example.recipecollectionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "RecipeApp.db";
    // Version 2 forces a reset to apply the new recipe table
    public static final int DB_VERSION = 2;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RECIPES = "recipes";

    // Recipe Columns
    public static final String COL_RECIPE_TITLE = "title";

    public DBHelper(Context context) {
        super(context, DBNAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table (added an integer id for session handling)
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, name TEXT)");

        // Create Recipes Table
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, ingredients TEXT, steps TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- USER METHODS ---

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // Returns User ID if valid, -1 if invalid
    public int checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE email = ? AND password = ?", new String[]{email, password});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    // --- RECIPE METHODS ---

    public boolean addRecipe(int userId, String title, String ingredients, String steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("title", title);
        cv.put("ingredients", ingredients);
        cv.put("steps", steps);
        return db.insert(TABLE_RECIPES, null, cv) != -1;
    }

    public Cursor getUserRecipes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // SimpleCursorAdapter REQUIRES "id AS _id"
        return db.rawQuery("SELECT id AS _id, title FROM " + TABLE_RECIPES + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }

    public Cursor getRecipeDetails(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE id = ?", new String[]{String.valueOf(recipeId)});
    }

    public void deleteRecipe(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPES, "id = ?", new String[]{String.valueOf(recipeId)});
    }
}