package com.example.user.skillbarter;

import com.google.firebase.Timestamp;

public class Appointment {
    private String providerUID, clientUID, skillID;
    private Timestamp date;
    private int ranking;
    private boolean isProviderPaid;

    public Appointment(String providerUID, String clientUID, String skillID, Timestamp date, int ranking, boolean isProviderPaid) {
        this.providerUID = providerUID;
        this.clientUID = clientUID;
        this.skillID = skillID;
        this.date = date;
        this.ranking = ranking;
        this.isProviderPaid = isProviderPaid;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public boolean isProviderPaid() {
        return isProviderPaid;
    }

    public void setProviderPaid(boolean providerPaid) {
        isProviderPaid = providerPaid;
    }
}
