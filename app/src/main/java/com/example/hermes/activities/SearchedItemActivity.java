package com.example.hermes.activities;
import android.graphics.Color;
import android.util.TypedValue;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hermes.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Category;
import entities.Listing;
import utils.CategoryListingAdapter;
import com.example.hermes.activities.ListItem;
import com.example.hermes.activities.ListingDetails;

public class SearchedItemActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<ListItem> items; // Declare items list for use in methods
    private String buyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searched_item);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        TextView categoryCountTextView = findViewById(R.id.categoryCount);
        LinearLayout categoryButtonsContainer = findViewById(R.id.categoryButtonsContainer);
        ListView listingsListView = findViewById(R.id.listingsListView);
        Spinner categoryDropdown = findViewById(R.id.categoryDropdown);
        TextInputEditText searchInput = findViewById(R.id.listingSearchInput);
        ImageView searchIcon = findViewById(R.id.searchIcon);

        // Get the search query and category from the Intent
        String initialSearchQuery = getIntent().getStringExtra("searchField");
        String initialCategory = getIntent().getStringExtra("category");
        buyerName = getIntent().getStringExtra("name");

        // Log received intent values for debugging
        Log.d("SearchedItemActivity", "Search Query: " + initialSearchQuery + ", Category: " + initialCategory);

        // Populate category dropdown
        populateCategoryDropdown(categoryDropdown);

        // Fetch initial data
        fetchAndDisplayData(initialSearchQuery, initialCategory, categoryCountTextView, categoryButtonsContainer, listingsListView);

        // Search button functionality
        searchIcon.setOnClickListener(v -> {
            String newSearchQuery = searchInput.getText().toString();
            String selectedCategory = categoryDropdown.getSelectedItem().toString();

            if (selectedCategory.equals("Select a category")) {
                selectedCategory = null;
            }

            if (newSearchQuery.isEmpty()) {
                Toast.makeText(SearchedItemActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            } else {
                fetchAndDisplayData(newSearchQuery, selectedCategory, categoryCountTextView, categoryButtonsContainer, listingsListView);
            }
        });
    }

    private void populateCategoryDropdown(Spinner categoryDropdown) {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> categories = new ArrayList<>();
                        categories.add("Select a category");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("categoryName");
                            if (categoryName != null) {
                                categories.add(categoryName);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                categories
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categoryDropdown.setAdapter(adapter);
                    } else {
                        Log.e("SearchedItemActivity", "Error fetching categories: ", task.getException());
                        Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAndDisplayData(String searchQuery, String category,
                                     TextView categoryCountTextView,
                                     LinearLayout categoryButtonsContainer,
                                     ListView listingsListView) {
        fetchListingsFromFirestore(searchQuery, category, new FirebaseCallback() {
            @Override
            public void onSuccess(Map<Category, List<Listing>> categoryToListings) {
                if (categoryToListings.isEmpty()) {
                    categoryCountTextView.setText("No results found.");
                    categoryButtonsContainer.removeAllViews();
                    listingsListView.setAdapter(null);
                    return;
                }

                // Update category count
                categoryCountTextView.setText(categoryToListings.size() + " categories found");

                // Prepare a combined list of category headers and listings
                items = new ArrayList<>();

                for (Map.Entry<Category, List<Listing>> entry : categoryToListings.entrySet()) {
                    Category cat = entry.getKey();
                    List<Listing> listings = entry.getValue();

                    // Add category header
                    items.add(new ListItem(cat));

                    // Add listings under this category
                    for (Listing listing : listings) {
                        items.add(new ListItem(listing));
                    }
                }

                // Set the adapter
                CategoryListingAdapter adapter = new CategoryListingAdapter(SearchedItemActivity.this, items);
                listingsListView.setAdapter(adapter);

                listingsListView.setOnItemClickListener((parent, view, position, id) -> {
                    ListItem clickedItem = items.get(position);
                    if (clickedItem.getType() == ListItem.TYPE_LISTING) {

                        // get listing
                        Listing listing = clickedItem.getListing();
                        
                        Intent intent = new Intent(SearchedItemActivity.this, ListingDetails.class);
                        
                        // pass data
                        intent.putExtra("listing", listing);
                        intent.putExtra("name", buyerName);
                        
                        // start
                        startActivity(intent);
                    }
                });

                // Populate category buttons

                categoryButtonsContainer.removeAllViews();
                for (Category cat : categoryToListings.keySet()) {
                    Button categoryButton = new Button(SearchedItemActivity.this);
                    categoryButton.setText(cat.getCategoryName());

                    // Set the background color to hex color #f3ce92
                    categoryButton.setBackgroundColor(Color.parseColor("#f3ce92"));

                    // Set text color for better visibility
                    categoryButton.setTextColor(Color.BLACK);

                    // Optional: Adjust padding and text size
                    categoryButton.setPadding(16, 8, 16, 8); // Left, Top, Right, Bottom
                    categoryButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                    categoryButton.setOnClickListener(v -> {
                        // Scroll to the category header in the list
                        int position = findCategoryPosition(items, cat);
                        if (position != -1) {
                            listingsListView.smoothScrollToPosition(position);
                        }
                    });
                    categoryButtonsContainer.addView(categoryButton);
                }

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SearchedItemActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("FirestoreError", "Error fetching listings: ", e);
            }
        });
    }

    // Helper method to find the position of a category in the items list
    private int findCategoryPosition(List<ListItem> items, Category category) {
        for (int i = 0; i < items.size(); i++) {
            ListItem item = items.get(i);
            if (item.getType() == ListItem.TYPE_CATEGORY && item.getCategory().equals(category)) {
                return i;
            }
        }
        return -1; // Category not found
    }

    private void fetchListingsFromFirestore(String searchQuery, String category, FirebaseCallback callback) {
        Query query = db.collection("listings").whereEqualTo("name", searchQuery);

        if (category != null) {
            query = query.whereEqualTo("category", category);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<Category, List<Listing>> categoryToListings = new HashMap<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String name = document.getString("name");
                        String categoryName = document.getString("category");
                        String description = document.getString("description");
                        double price = document.contains("price") ? document.getDouble("price") : 0.0;
                        String sellerName = document.getString("sellerName");
                        String imageUrl = document.getString("imageUrl");
                        String listingID = document.getId(); // Use document ID as listingID

                        // Validate fields in Listing constructor
                        Listing listing = new Listing(name, categoryName, description, sellerName, price, null, null, imageUrl, listingID);
                        Category cat = new Category(categoryName, "Description of " + categoryName, categoryName);

                        categoryToListings.putIfAbsent(cat, new ArrayList<>());
                        categoryToListings.get(cat).add(listing);
                    } catch (IllegalArgumentException e) {
                        Log.e("FirestoreError", "Error parsing document", e);
                    }
                }
                callback.onSuccess(categoryToListings);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    interface FirebaseCallback {
        void onSuccess(Map<Category, List<Listing>> categoryToListings);

        void onFailure(Exception e);
    }
}
