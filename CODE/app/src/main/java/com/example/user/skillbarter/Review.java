package com.example.user.skillbarter;

import com.google.firebase.Timestamp;

public class Review {
    private String reviewerName;
    private String reviewerPicture;
    private Timestamp dateOfReview;
    private int rank;
    private String details;

    public Review() {
    }

    public Review(String reviewerID, String reviewerPicture, Timestamp dateOfReview, int rank, String details) {
        this.reviewerName = reviewerID;
        this.reviewerPicture = reviewerPicture;
        this.dateOfReview = dateOfReview;
        this.rank = rank;
        this.details = details;
    }


    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerPicture() {
        return reviewerPicture;
    }

    public void setReviewerPicture(String reviewerPicture) {
        this.reviewerPicture = reviewerPicture;
    }

    public Timestamp getDateOfReview() {
        return dateOfReview;
    }

    public void setDateOfReview(Timestamp dateOfReview) {
        this.dateOfReview = dateOfReview;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
