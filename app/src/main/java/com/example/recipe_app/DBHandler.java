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
    // We bumped this to 3 so the phone knows we changed the "drawers" in our cabinet
    private static final int DB_VERSION = 3;

    // User Table Stuff
    private static final String TABLE_USERS = "users";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EMAIL = "email";

    // Recipe Table Stuff
    private static final String TABLE_RECIPES = "recipes";
    private static final String COL_RECIPE_NAME = "recipe_name";
    private static final String COL_INGREDIENTS = "ingredients";
    private static final String COL_INSTRUCTIONS = "instructions";
    private static final String COL_TOTAL_TIME = "total_time";
    private static final String COL_USER_COMMENT = "user_comment";
    // This is the "Name Tag" column so recipes know who they belong to!
    private static final String COL_RECIPE_OWNER = "recipe_owner";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USERNAME + " TEXT PRIMARY KEY, "
                + COL_EMAIL + " TEXT, "
                + COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // We added COL_RECIPE_OWNER at the end here
        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT, "
                + COL_INGREDIENTS + " TEXT, "
                + COL_INSTRUCTIONS + " TEXT, "
                + COL_TOTAL_TIME + " TEXT, "
                + COL_USER_COMMENT + " TEXT,"
                + COL_RECIPE_OWNER + " TEXT)";
        db.execSQL(createRecipesTable);
    }

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
            String realUsername = cursor.getString(0); // This gets the actual Username!
            cursor.close();
            return realUsername;
        } else {
            cursor.close();
            return null; // Login failed
        }
    }

    // FIXED: Now takes a "String owner" so it only counts YOUR recipes!
    public int getRecipesCount(String owner) {
        int count = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES +
                    " WHERE " + COL_RECIPE_OWNER + " = ?", new String[]{owner});
            count = cursor.getCount();
            cursor.close();
        } catch (Exception e) {
            return 0;
        }
        return count;
    }

    // FIXED: Added "String owner" at the end so it saves the name tag!
    public boolean insertRecipe(String name, String ing, String method, String time, String comment, String owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_INGREDIENTS, ing);
        values.put(COL_INSTRUCTIONS, method);
        values.put(COL_TOTAL_TIME, time);
        values.put(COL_USER_COMMENT, comment);
        values.put(COL_RECIPE_OWNER, owner); // Saves who made it

        long result = db.insert(TABLE_RECIPES, null, values);
        return result != -1;
    }

    // FIXED: Added "String owner" so the list only shows YOUR food
    public List<String> getAllRecipeNames(String owner) {
        List<String> recipeNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_RECIPE_NAME + " FROM " + TABLE_RECIPES +
                " WHERE " + COL_RECIPE_OWNER + " = ?", new String[]{owner});

        if (cursor.moveToFirst()) {
            do {
                recipeNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipeNames;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    public Cursor getRecipeDetails(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_NAME + " = ?", new String[]{name});
    }

    public void deleteRecipe(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPES, COL_RECIPE_NAME + " = ?", new String[]{name});
        db.close();
    }

    public boolean updateRecipe(String originalName, String newName, String ingredients, String method, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, newName);
        values.put(COL_INGREDIENTS, ingredients);
        values.put(COL_INSTRUCTIONS, method);
        values.put(COL_TOTAL_TIME, time);
        values.put(COL_USER_COMMENT, comment);
        int result = db.update(TABLE_RECIPES, values, COL_RECIPE_NAME + " = ?", new String[]{originalName});
        return result > 0;
    }
    public boolean updatePassword(String identity, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PASSWORD, newPassword);

        // This part looks for the user by their name OR their email
        int result = db.update(TABLE_USERS, contentValues,
                COL_USERNAME + " = ? OR " + COL_EMAIL + " = ?",
                new String[]{identity, identity});

        return result > 0; // If it fixed at least 1 person, return true!
    }
}