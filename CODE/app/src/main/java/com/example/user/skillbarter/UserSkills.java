package com.example.user.skillbarter;


public class UserSkills {

    private String  userID;
    private String skillId;
    private int level;
    private double ranking;
    private int pointsValue;
    private String address;

    public UserSkills(String userID, String skillId, int level, int pointsValue, String address) {
        this.userID = userID;
        this.skillId = skillId;
        this.level = level;
        this.pointsValue = pointsValue;
        this.address = address;
    }

    public UserSkills(String userID, String skillId, int level, int pointsValue) {
        this.userID = userID;
        this.skillId = skillId;
        this.level = level;
        this.pointsValue = pointsValue;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getRanking() {
        return ranking;
    }

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        this.pointsValue = pointsValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
