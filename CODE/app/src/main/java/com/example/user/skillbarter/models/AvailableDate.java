package com.example.user.skillbarter.models;

import java.util.Date;
//TODO: remove available field
public class AvailableDate {
    private Date date;
    private boolean available;
    private boolean booked;

    public AvailableDate() {}

    public AvailableDate(Date date) {
        this.date = date;
        this.available = true;
        this.booked = false;
    }

    public AvailableDate(Date date, boolean isAvailable, boolean isBooked) {
        this.date = date;
        this.available = isAvailable;
        this.booked = isBooked;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
