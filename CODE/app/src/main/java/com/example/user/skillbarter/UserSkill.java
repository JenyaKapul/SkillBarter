package com.example.user.skillbarter;


public class UserSkill {

    private String skillId;
    private String  userID;
    private String category;
    private String skill;
    private int pointsValue;
    private int level;
    private String details;
    private float ranking;

    public UserSkill() {}

    public UserSkill(String userID, String category, String skill, int pointsValue, int level, String details) {
        this.userID = userID;
        this.category = category;
        this.skill = skill;
        this.skillId = userID + "." + category + "." + skill;
        this.pointsValue = pointsValue;
        this.level = level;
        this.details = details;
    }


    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = category + "." + skill;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        this.pointsValue = pointsValue;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public float getRanking() {
        return ranking;
    }

    public void setRanking(float ranking) {
        this.ranking = ranking;
    }
}
