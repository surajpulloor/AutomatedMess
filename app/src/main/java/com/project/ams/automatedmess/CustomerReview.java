package com.project.ams.automatedmess;

public class CustomerReview {
    private float rating;
    private String review;
    private String reviewGivenTo;

    public CustomerReview() {
    }

    public CustomerReview(int rating, String review, String reviewGivenTo) {
        this.rating = rating;
        this.review = review;
        this.reviewGivenTo = reviewGivenTo;
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

    public String getReviewGivenTo() {
        return reviewGivenTo;
    }

    public void setReviewGivenTo(String reviewGivenTo) {
        this.reviewGivenTo = reviewGivenTo;
    }
}
