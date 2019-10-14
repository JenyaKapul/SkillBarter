package com.example.user.skillbarter.models;

import java.util.Date;
public class AvailableDate {
    private Date date;
    private boolean booked;

    public AvailableDate() {}

    public AvailableDate(Date date) {
        this.date = date;
        this.booked = false;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
