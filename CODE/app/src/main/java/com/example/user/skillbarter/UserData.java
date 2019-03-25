package com.example.user.skillbarter;

public class UserData {

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String userID;

    private String userName;

    private String email;


    public UserData(String userID, String userName, String email) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
    }
}
