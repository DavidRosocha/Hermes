package entities;

import java.util.ArrayList;

/**
 * represents a shopping cart in the system
 */
public class Cart {
    /**
     * the unique identifier for the cart
     */
    private String cartId;

    /**
     * the buyer associated with this cart
     */
    private Buyer buyer;

    /**
     * the list of items in the cart
     */
    private ArrayList<Listing> items;

    /**
     * the total price of all items in the cart
     */
    private double totalPrice;

    /**
     * constructs a new cart for the buyer
     *
     * @param buyer the buyer associated with this cart
     */
    public Cart(Buyer buyer) {
        this.buyer = buyer;
        this.totalPrice = 0;
        this.items = new ArrayList<>(); // Initialize the items list
    }

    /**
     * gets the buyer associated with this cart
     *
     * @return the buyer associated with this cart
     */
    public Buyer getBuyer() {
        return buyer;
    }

    /**
     * gets the total price of all items in the cart
     *
     * @return the total price of all items in the cart
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * adds an item to the cart
     *
     * @param listing the listing to be added to the cart
     */
    public void addItem(Listing listing) {
        items.add(listing);
        System.out.println("listing has been added to cart");
    }

    /**
     * getter for the cartID
     *
     */
    public String getCartId()
    {
        return cartId;
    }

    /**
     * removes an item from the cart
     *
     * @param listing the listing to be removed from the cart
     */
    public void removeItem(Listing listing) {
        items.remove(listing);
        System.out.println("Listing has been removed from cart");
    }

    /**
     * gets a string representation of all items in the cart
     *
     * @return a string representation of all items in the cart
     */
    public String getItems() {
        StringBuilder itemListString = new StringBuilder();
        for (Listing listing : items) {
            // Add each listing to the itemListString
            // This part needs to be implemented
            itemListString.append(listing); // This should be changed to return itemListString.toString() after implementation
        }
        return "No Items yet";
    }

    /**
     * clears all items from the cart
     */
    public void clearCart() {
        items.clear(); // Actually clear the items list
        totalPrice = 0; // Reset the total price
        System.out.println("cart has been cleared");
    }
}