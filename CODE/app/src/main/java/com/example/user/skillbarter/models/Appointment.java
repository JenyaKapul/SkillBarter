package com.example.user.skillbarter.models;

import java.util.Date;

public class Appointment {
    private String providerUID, clientUID, skillID;
    private Date date;
    private int rating;
    private boolean providerPaid;
    private int points;

    Appointment() {}

    public Appointment(String providerUID, String clientUID, String skillID, Date date,
                       int rating, boolean providerPaid, int points) {
        this.providerUID = providerUID;
        this.clientUID = clientUID;
        this.skillID = skillID;
        this.date = date;
        this.rating = rating;
        this.providerPaid = providerPaid;
        this.points = points;
    }

    public String getProviderUID() {
        return providerUID;
    }

    public void setProviderUID(String providerUID) {
        this.providerUID = providerUID;
    }

    public String getClientUID() {
        return clientUID;
    }

    public void setClientUID(String clientUID) {
        this.clientUID = clientUID;
    }

    public String getSkillID() {
        return skillID;
    }

    public void setSkillID(String skillID) {
        this.skillID = skillID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public boolean isProviderPaid() {
        return providerPaid;
    }

    public void setProviderPaid(boolean providerPaid) {
        this.providerPaid = providerPaid;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
