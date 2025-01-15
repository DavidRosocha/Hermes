package entities;

import java.io.Serializable;

/**
 * represents a buyer in the system, extending the account class
 */
public class Buyer extends Account {
    /**
     * the unique identifier for the buyer
     */
    private final String buyerId;

    /**
     * the shopping cart associated with the buyer
     */
    private final Cart cart;

    /**
     * a counter used to generate unique buyer ids
     */
    private static int buyerIdMemory = 1;

    /**
     * constructs a new buyer with all details
     *
     * @param name                 the name of the buyer
     * @param email                the email address of the buyer
     * @param password             the password for the buyer account
     * @param phoneNumber          the phone number of the buyer
     * @param profilePicture       the profile picture of the buyer
     * @param accountCreationDate  the date when the buyer account was created
     * @param age                  the age of the buyer
     * @param residenceCity        the city where the buyer resides
     * @param buyerId              the unique identifier for the buyer
     * @param cart                 the shopping cart associated with the buyer
     */
    public Buyer(String name, String email, String password, String phoneNumber,
                 byte[] profilePicture, long accountCreationDate, int age, String residenceCity, String buyerId, Cart cart) {

        super(name, email, password, "buyer", phoneNumber, profilePicture, accountCreationDate, age, residenceCity, buyerId);

        this.buyerId = buyerId;
        this.cart = cart;
    }

    /**
     * constructs a new buyer with basic details
     *
     * @param name     the name of the buyer
     * @param email    the email address of the buyer
     * @param password the password for the buyer account
     */
    public Buyer(String name, String email, String password) {

        super(name, email, password, "buyer");

        this.buyerId = String.valueOf(buyerIdMemory);
        buyerIdMemory++;
        this.cart = new Cart(this);
    }

    /**
     * gets the buyer id
     *
     * @return the unique identifier for the buyer
     */
    public String getBuyerID() {
        return buyerId;
    }

    /**
     * adds a listing to the buyer's cart
     *
     * @param listing the listing to be added to the cart
     */
    public void addToCart(Listing listing) {
        cart.addItem(listing);
    }

    /**
     * removes a listing from the buyer's cart
     *
     * @param listing the listing to be removed from the cart
     */
    public void removeFromCart(Listing listing) {
        cart.removeItem(listing);
    }

    /**
     * purchases all items in the buyer's cart
     */
    public void purchaseItems() {
        // Implementation for purchasing items in the cart
        System.out.println("Items purchased: " + cart.getItems());
        cart.clearCart(); // Clear the cart after purchase
    }

    /**
     * leaves a review for a listing
     *
     * @param listing the listing to be reviewed
     * @param review  the review to be added to the listing
     */
    public void leaveReview(Listing listing, Review review) {
        listing.addReview(review);
    }

    // Leave a message (TBD)
    /*
    /**
     * leaves a message (e.g., to the seller)
     *
     * @param message the message to be sent
     *
    public void leaveMessage(Message message) {
        // Implementation for leaving a message (e.g., to the seller)
        System.out.println("Message sent: " + message.getContent());
    }
    */
}