package com.example.user.skillbarter;

public class ReviewItem {
    private int mImageResource;
    private String mReviewerName;
    private int mRank;
    private String mDate;
    private String mReviewContent;

    public ReviewItem(int ImageResource, String ReviewerName, int Rank, String Date, String ReviewContent){
        mImageResource = ImageResource;
        mReviewerName = ReviewerName;
        mRank = Rank;
        mDate = Date;
        mReviewContent = ReviewContent;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getReviewerName() {
        return mReviewerName;
    }

    public int getRank() {
        return mRank;
    }

    public String getDate() {
        return mDate;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

}
