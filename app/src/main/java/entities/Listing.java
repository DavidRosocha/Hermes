package entities;

import java.util.ArrayList;
import java.io.Serializable;


/**
 * Represents a listing in the system.
 */
public class Listing implements Serializable{
    private String name;
    private String category;
    private String description;
    private double price;
    private String listingID;
    private String sellerName;
    private Boolean isActive;
    private String imageUrl;
    private ArrayList<Review> reviews;
    private String startDate;
    private String endDate;

    public Listing() {}

    /**
     * Constructs a new listing.
     *
     * @param name  the name of the listing
     * @param category the category name of the listing
     * @param description  the description of the listing
     * @param price        the price of the listing
     * @param imageUrl      the photos associated with the listing
     * @param listingID    the unique identifier for the listing
     */
    public Listing(String name, String category, String description, String sellerName, double price, String startDate, String endDate, String imageUrl, String listingID) {
        validateFields(name, category, description, sellerName, price, startDate, endDate, imageUrl, listingID);

        this.name = name;
        this.category = category;
        this.description = description;
        this.listingID = listingID;
        this.price = price;
        this.isActive = true;
        this.imageUrl = imageUrl;
        this.sellerName = sellerName;
        // Use default values for missing dates
        this.startDate = startDate != null ? startDate : "N/A";
        this.endDate = endDate != null ? endDate : "N/A";
        this.reviews = new ArrayList<>();
    }


    /**
     * Validates required fields for the listing.
     */
    private void validateFields(String name, String category, String description, String sellerName, double price, String startDate, String endDate, String imageUrl, String listingID) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (sellerName == null || sellerName.isEmpty()) {
            throw new IllegalArgumentException("Seller name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL is required.");
        }
        if (listingID == null || listingID.isEmpty()) {
            throw new IllegalArgumentException("Listing ID is required.");
        }
        // `startDate` and `endDate` are no longer validated as required fields
    }


    public String getListingID() {
        return listingID;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }



    public void addReview(Review review) {
        reviews.add(review);
    }

    @Override
    public String toString() {
        return "Listing{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", price=" + price +
                ", startDate='" + startDate + '\'' +
                ", endDate" + endDate + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", listingID='" + listingID + '\'' +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getSellerName(){

        return sellerName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStartDate() {return startDate;}

    public String getEndDate() {return endDate;}

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
