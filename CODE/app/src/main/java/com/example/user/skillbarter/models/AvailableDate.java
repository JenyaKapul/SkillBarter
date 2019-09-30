package com.example.user.skillbarter.models;

import java.util.Date;

public class AvailableDate {
    private Date date;
    private boolean isValid;
    private boolean isBooked;

    public AvailableDate() {}

    public AvailableDate(Date date) {
        this.date = date;
        this.isValid = true;
        this.isBooked = false;
    }

    public AvailableDate(Date date, boolean isValid, boolean isBooked) {
        this.date = date;
        this.isValid = isValid;
        this.isBooked = isBooked;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public boolean isAvailable() {
        return isValid && !isBooked;
    }
}
