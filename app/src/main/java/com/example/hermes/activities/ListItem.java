// ListItem.java
package com.example.hermes.activities;

import entities.Category;
import entities.Listing;

public class ListItem {
    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_LISTING = 1;

    private int type;
    private Category category;
    private Listing listing;

    // Constructor for category header
    public ListItem(Category category) {
        this.type = TYPE_CATEGORY;
        this.category = category;
    }

    // Constructor for listing item
    public ListItem(Listing listing) {
        this.type = TYPE_LISTING;
        this.listing = listing;
    }

    public int getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public Listing getListing() {
        return listing;
    }
}
