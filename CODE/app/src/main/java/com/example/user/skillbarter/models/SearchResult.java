package com.example.user.skillbarter.models;

public class SearchResult {
    private int value;
    private float rating;
    private String providerName, providerUID, providerImageURL, skillName, categoryName;

    public SearchResult(int value, float rating, String providerName, String providerUID, String providerImageURL, String skillName, String categoryName) {
        this.value = value;
        this.rating = rating;
        this.providerName = providerName;
        this.providerUID = providerUID;
        this.providerImageURL = providerImageURL;
        this.skillName = skillName;
        this.categoryName = categoryName;
    }

    public String getProviderUID() {
        return providerUID;
    }

    public void setProviderUID(String providerUID) {
        this.providerUID = providerUID;
    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getRating() { return rating; }

    public void setRating(float rating) { this.rating = rating; }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderImageURL() {
        return providerImageURL;
    }

    public void setProviderImageURL(String providerImageURL) {
        this.providerImageURL = providerImageURL;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategoryName() { return categoryName; }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
