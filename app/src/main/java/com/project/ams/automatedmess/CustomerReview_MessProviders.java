package com.project.ams.automatedmess;

public class CustomerReview_MessProviders {
    private float rating;
    private String review;
    private String reviewGivenBy;

    public CustomerReview_MessProviders(int rating, String review, String reviewGivenBy) {
        this.rating = rating;
        this.review = review;
        this.reviewGivenBy = reviewGivenBy;
    }

    public CustomerReview_MessProviders() {
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewGivenBy() {
        return reviewGivenBy;
    }

    public void setReviewGivenBy(String reviewGivenBy) {
        this.reviewGivenBy = reviewGivenBy;
    }
}
