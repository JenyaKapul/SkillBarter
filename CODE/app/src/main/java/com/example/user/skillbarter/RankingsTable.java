package com.example.user.skillbarter;

import com.google.firebase.Timestamp;

public class RankingsTable {
    private String userID1;
    private String userID2;
    private String skillId;
    private double ranking;
    private Timestamp dateOfService;
    private String details;

    public RankingsTable(String userID1, String userID2, String skillId, double ranking,
                         Timestamp dateOfService) {
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.skillId = skillId;
        this.ranking = ranking;
        this.dateOfService = dateOfService;
    }

    public RankingsTable(String userID1, String userID2, String skillId, double ranking,
                         Timestamp dateOfService, String details) {
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.skillId = skillId;
        this.ranking = ranking;
        this.dateOfService = dateOfService;
        this.details = details;
    }

    public String getUserID1() {
        return userID1;
    }

    public String getUserID2() {
        return userID2;
    }

    public String getSkillId() {
        return skillId;
    }

    public double getRanking() {
        return ranking;
    }

    public Timestamp getDateOfService() {
        return dateOfService;
    }

    public String getDetails() {
        return details;
    }
    
}
