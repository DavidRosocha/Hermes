package entities;

import com.google.firebase.Timestamp;

public class BuyerRequest {
    private String id;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private String listingId;
    private String listingName;
    private Timestamp timestamp;
    private String status;

    // Default constructor required for Firestore deserialization
    public BuyerRequest() {}

    // Constructor for creating new requests
//    public BuyerRequest(String buyerId, String buyerName, String sellerId, String sellerName,
//                        String listingId, String listingName, String message, Timestamp timestamp,
//                        String status) {
//        this.buyerId = buyerId;
//        this.buyerName = buyerName;
//        this.sellerId = sellerId;
//        this.sellerName = sellerName;
//        this.listingId = listingId;
//        this.listingName = listingName;
//        this.message = message;
//        this.timestamp = timestamp;
//        this.status = status;
//    }

    public BuyerRequest( String id, String buyerName, String sellerName,
                        String listingId, String listingName, String message, Timestamp timestamp,
                        String status) {
        this.id = id;
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.listingId = listingId;
        this.listingName = listingName;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String listingName) {
        this.listingName = listingName;
    }



    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Optional: Add toString() for debugging
    @Override
    public String toString() {
        return
                "listing:" + listingName + '\'' +
                "buyer Name: " + buyerName;
    }
}
