package com.example.user.skillbarter;

public class SearchResult {
    private int value;
    private float ranking;
    private String providerName, providerImageURL, skillName, categoryName;

    public SearchResult(int value, float ranking, String providerName, String providerImageURL, String skillName, String categoryName) {
        this.value = value;
        this.ranking = ranking;
        this.providerName = providerName;
        this.providerImageURL = providerImageURL;
        this.skillName = skillName;
        this.categoryName = categoryName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getRanking() { return ranking; }

    public void setRanking(float ranking) { this.ranking = ranking; }

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
