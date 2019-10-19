package com.example.user.skillbarter.models;

import java.util.Date;

public class UserData {

    private String userID;
    private String profilePictureURL;
    private Date dateOfBirth;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String email;
    private String gender;
    private int pointsBalance;
    private float avgRating;
    private int numOfRatings;

    public UserData(String userID, String profilePictureURL,
                    Date dateOfBirth, String firstName, String lastName,
                    String phoneNumber, String address, String email,
                    String gender, int pointsBalance) {
        this.userID = userID;
        this.profilePictureURL = profilePictureURL;
        this.dateOfBirth = dateOfBirth;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.pointsBalance = pointsBalance;
        this.avgRating = 0;
        this.numOfRatings = 0;
    }

    public UserData() {}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float personalRating) {
        this.avgRating = personalRating;
    }

    public int getNumOfRatings() {
        return numOfRatings;
    }

    public void setNumOfRatings(int numOfRatings) {
        this.numOfRatings = numOfRatings;
    }
}
