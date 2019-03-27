package com.example.user.skillbarter;

import com.google.firebase.Timestamp;

enum Status {
    AVAILABLE, BOOKED, DISABLED
}

public class TimeSlots {
    private String userID1;
    private String userID2;
    private Status status;
    private String skillID;
    private Timestamp startTime;
    private Timestamp endTime;


    public TimeSlots(String userID1, String userID2, Status status, String skillID,
                     Timestamp startTime, Timestamp endTime) {
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.status = status;
        this.skillID = skillID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUserID1() {
        return userID1;
    }

    public void setUserID1(String userID1) {
        this.userID1 = userID1;
    }

    public String getUserID2() {
        return userID2;
    }

    public void setUserID2(String userID2) {
        this.userID2 = userID2;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSkillID() {
        return skillID;
    }

    public void setSkillID(String skillID) {
        this.skillID = skillID;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
