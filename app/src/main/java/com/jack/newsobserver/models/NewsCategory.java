package com.jack.newsobserver.models;

import java.util.List;

public class NewsCategory {
    private long categoryId;
    private String categoryName;
    private List<NewsTopic> categoryTopics;


    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<NewsTopic> getCategoryTopics() {
        return categoryTopics;
    }

    public void setCategoryTopics(List<NewsTopic> categoryTopics) {
        this.categoryTopics = categoryTopics;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
