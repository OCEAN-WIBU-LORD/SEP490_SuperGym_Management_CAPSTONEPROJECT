package com.supergym.sep490_supergymmanagement.models;

public class PostCategory {
    private String categoryId;
    private String name; // Make sure this matches the JSON response

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

