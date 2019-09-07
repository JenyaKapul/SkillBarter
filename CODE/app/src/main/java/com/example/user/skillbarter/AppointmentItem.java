package com.example.user.skillbarter;

import com.google.firebase.Timestamp;

public class AppointmentItem {
    private int value;
    private Timestamp date;
    private String profileName, profileImageURL, skillName, categoryName;

    public AppointmentItem(int value, Timestamp date, String profileName, String profileImageURL, String skillName, String categoryName) {
        this.value = value;
        this.date = date;
        this.profileName = profileName;
        this.profileImageURL = profileImageURL;
        this.skillName = skillName;
        this.categoryName = categoryName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategoryName() {
        return "(" + categoryName + ")";
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
