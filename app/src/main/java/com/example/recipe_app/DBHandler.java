package com.example.recipe_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "RecipeDB";
    // පින්තූර කොලම් එක එකතු කරපු නිසා Version එක 4 කළා
    private static final int DB_VERSION = 4;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EMAIL = "email";

    // Recipe Table
    private static final String TABLE_RECIPES = "recipes";
    private static final String COL_RECIPE_NAME = "recipe_name";
    private static final String COL_INGREDIENTS = "ingredients";
    private static final String COL_INSTRUCTIONS = "instructions";
    private static final String COL_TOTAL_TIME = "total_time";
    private static final String COL_USER_COMMENT = "user_comment";
    private static final String COL_RECIPE_OWNER = "recipe_owner";
    private static final String COL_IMAGE_PATH = "image_path"; // අලුත් Column එක

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table එක හැදීම
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USERNAME + " TEXT PRIMARY KEY, "
                + COL_EMAIL + " TEXT, "
                + COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Recipes Table එක හැදීම (පින්තූර පථයත් එක්කම)
        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT, "
                + COL_INGREDIENTS + " TEXT, "
                + COL_INSTRUCTIONS + " TEXT, "
                + COL_TOTAL_TIME + " TEXT, "
                + COL_USER_COMMENT + " TEXT,"
                + COL_RECIPE_OWNER + " TEXT,"
                + COL_IMAGE_PATH + " TEXT)";
        db.execSQL(createRecipesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- යූසර් සම්බන්ධ වැඩ ---

    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public String checkUserLogin(String identity, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE ("
                + COL_USERNAME + " = ? OR " + COL_EMAIL + " = ?) AND "
                + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{identity, identity, password});

        if (cursor.moveToFirst()) {
            String realUsername = cursor.getString(0);
            cursor.close();
            return realUsername;
        } else {
            cursor.close();
            return null;
        }
    }

    public boolean updatePassword(String identity, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, contentValues,
                COL_USERNAME + " = ? OR " + COL_EMAIL + " = ?",
                new String[]{identity, identity});
        return result > 0;
    }

    // --- රෙසිපි සම්බන්ධ වැඩ ---

    public boolean insertRecipe(String name, String ing, String method, String time, String comment, String owner, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_INGREDIENTS, ing);
        values.put(COL_INSTRUCTIONS, method);
        values.put(COL_TOTAL_TIME, time);
        values.put(COL_USER_COMMENT, comment);
        values.put(COL_RECIPE_OWNER, owner);
        values.put(COL_IMAGE_PATH, imagePath); // Image Path එක සේව් කරනවා

        long result = db.insert(TABLE_RECIPES, null, values);
        return result != -1;
    }

    public boolean updateRecipe(String originalName, String newName, String ingredients, String method, String time, String comment, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, newName);
        values.put(COL_INGREDIENTS, ingredients);
        values.put(COL_INSTRUCTIONS, method);
        values.put(COL_TOTAL_TIME, time);
        values.put(COL_USER_COMMENT, comment);
        values.put(COL_IMAGE_PATH, imagePath);
        int result = db.update(TABLE_RECIPES, values, COL_RECIPE_NAME + " = ?", new String[]{originalName});
        return result > 0;
    }

    public void deleteRecipe(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPES, COL_RECIPE_NAME + " = ?", new String[]{name});
        db.close();
    }

    public Cursor getRecipeDetails(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_NAME + " = ?", new String[]{name});
    }

    public int getRecipesCount(String owner) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_OWNER + " = ?", new String[]{owner});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // ලැයිස්තුව පෙන්නන්න අවශ්‍ය දත්ත Cursor එකක් ලෙස ලබා ගැනීම
    public Cursor getAllRecipesCursor(String owner) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id as _id, " + COL_RECIPE_NAME + ", " + COL_IMAGE_PATH + ", " + COL_TOTAL_TIME +
                " FROM " + TABLE_RECIPES +
                " WHERE " + COL_RECIPE_OWNER + " = ?", new String[]{owner});
    }
}