package com.example.user.skillbarter.models;

import java.util.Date;

public class AvailableDate {
    private Date date;
    private boolean valid;
    private boolean booked;

    public AvailableDate() {}

    public AvailableDate(Date date) {
        this.date = date;
        this.valid = true;
        this.booked = false;
    }

    public AvailableDate(Date date, boolean isValid, boolean isBooked) {
        this.date = date;
        this.valid = isValid;
        this.booked = isBooked;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean isAvailable() {
        return valid && !booked;
    }
}
