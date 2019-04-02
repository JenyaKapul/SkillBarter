package com.example.user.skillbarter;


public class UserSkills {

    private String skillId;
    private String  userID;
    private String category;
    private String skill;
//    private int level;
//    private float ranking;
    private int pointsValue;
//    private String address;


    public UserSkills() {
        //public no-arg constructor needed
    }

    public UserSkills(String userID, String category, String skill, int pointsValue) {
        this.userID = userID;
        this.category = category;
        this.skill = skill;
        this.skillId = userID + "." + category + "." + skill;
//        this.level = level;
        this.pointsValue = pointsValue;
//        this.address = address;
    }

//    public int getLevel() {
//        return level;
//    }

//    public void setLevel(int level) {
//        this.level = level;
//    }

//    public float getRanking() {
//        return ranking;
//    }

//    public void setRanking(float ranking) {
//        this.ranking = ranking;
//    }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        this.pointsValue = pointsValue;
    }

//    public String getAddress() {
//        return address;
//    }

//    public void setAddress(String address) {
//        this.address = address;
//    }

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = category + "." + skill;
    }
}
