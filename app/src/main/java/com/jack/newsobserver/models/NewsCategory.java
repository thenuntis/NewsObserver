package com.jack.newsobserver.models;

public class NewsCategory {
    private long categoryId;
    private String categoryName;
//    public List<NewsTopic> categoryTopics;


    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
