package com.example.recipecollectionapp;

public class Recipe {
    // 🔒 Private fields
    private int id;
    private int userId;
    private String title;
    private String ingredients;
    private String steps;

    // 🏗 Default Constructor
    public Recipe() {
    }

    // 🏗 Parameterized Constructor
    public Recipe(int id, int userId, String title, String ingredients, String steps) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // 📌 Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}

