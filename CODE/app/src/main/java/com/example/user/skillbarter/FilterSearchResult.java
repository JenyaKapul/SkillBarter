package com.example.user.skillbarter;

import android.os.Parcel;
import android.os.Parcelable;

// This class represents all search filter options chosen by user who's asking for service
public class FilterSearchResult implements Parcelable {
    private String category;
    private String skill;
    private int minPoints;
    private int maxPoints;
    private int minLevel;

    public FilterSearchResult(String category, String skill, int minPoints, int maxPoints, int minLevel) {
        this.category = category;
        this.skill = skill;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.minLevel = minLevel;
    }

    protected FilterSearchResult(Parcel in) {
        category = in.readString();
        skill = in.readString();
        minPoints = in.readInt();
        maxPoints = in.readInt();
        minLevel = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(skill);
        dest.writeInt(minPoints);
        dest.writeInt(maxPoints);
        dest.writeInt(minLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterSearchResult> CREATOR = new Creator<FilterSearchResult>() {
        @Override
        public FilterSearchResult createFromParcel(Parcel in) {
            return new FilterSearchResult(in);
        }

        @Override
        public FilterSearchResult[] newArray(int size) {
            return new FilterSearchResult[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }
}
