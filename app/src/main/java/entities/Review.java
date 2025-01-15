package entities;

import java.util.Date;

/**
 * represents a review in the system
 */
public class Review {
    /**
     * the unique identifier for the review
     */
    private String reviewId;

    /**
     * the title of the review
     */
    private String reviewTitle;

    /**
     * the buyer who wrote the review
     */
    private Buyer reviewer;

    /**
     * the account that was reviewed
     */
    private Account reviewedEntity;

    /**
     * the rating given in the review
     */
    private int rating;

    /**
     * the comments provided in the review
     */
    private String comments;

    /**
     * the timestamp when the review was created
     */
    private Date timestamp;

    /**
     * indicates whether the review is from a verified purchase
     */
    private Boolean isVerifiedPurchase;

    /**
     * constructs a new review
     *
     * @param reviewTitle    the title of the review
     * @param reviewer       the buyer who wrote the review
     * @param reviewedEntity the account that was reviewed
     * @param rating         the rating given in the review
     * @param comments       the comments provided in the review
     */
    public Review(String reviewTitle, Buyer reviewer, Account reviewedEntity, int rating, String comments) {
        this.reviewTitle = reviewTitle;
        this.reviewer = reviewer;
        this.reviewedEntity = reviewedEntity;
        this.rating = rating;
        this.comments = comments;
    }

    /**
     * gets the rating of the review
     *
     * @return the rating given in the review
     */
    public int getRating() {
        return rating;
    }
}