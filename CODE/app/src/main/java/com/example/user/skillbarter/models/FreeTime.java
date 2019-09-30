package com.example.user.skillbarter.models;

import java.util.Date;

public class FreeTime {
    private Date date;
    private boolean isAvailable;

    public FreeTime() {}

    public FreeTime(Date date, boolean isAvailable) {
        this.date = date;
        this.isAvailable = isAvailable;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
