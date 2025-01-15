package com.example.hermes.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hermes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import adapters.CategoryAdapter;
import data.FirebaseRepository;
import entities.Category;
import entities.Listing;

public class BuyerDashboard extends AppCompatActivity {

    private Spinner categorySpinner;
    private FirebaseRepository firebaseRepository;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories;
    private EditText searchInput;
    private ImageView searchIcon;
    private String buyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get buyer name 
        buyerName = getIntent().getStringExtra("name");

        // Initialize views
        categorySpinner = findViewById(R.id.categoryDropdown);
        searchInput = findViewById(R.id.listingSearchInput);
        searchIcon = findViewById(R.id.searchIcon);
        firebaseRepository = new FirebaseRepository();

        // Initialize RecyclerViews
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize categories list
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categories, firebaseRepository , buyerName);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Fetch categories and populate the spinner
        fetchCategoriesForSpinner();

        firebaseRepository.fetchCategoriesObject(fetchedCategories -> {
            if (fetchedCategories != null && !fetchedCategories.isEmpty()) {
                categories.clear();
                categories.addAll(fetchedCategories);
                categoryAdapter.notifyDataSetChanged();
            } else {
                Log.e("BuyerDashboard", "No categories fetched.");
            }
        }, error -> {
            Log.e("BuyerDashboard", "Error fetching categories: ", error);
        });

        // Add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                categoryRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable);
        }

        categoryRecyclerView.addItemDecoration(dividerItemDecoration);

        // set listener for the search icon
        searchIcon.setOnClickListener(view -> onClickSearchListing());
    }

    protected void onClickSearchListing() {
        // Create intent for SearchedItemActivity
        Intent intent = new Intent(BuyerDashboard.this, SearchedItemActivity.class);

        // Get the search content from the EditText
        String searchContent = searchInput.getText().toString();

        // Get the selected category from the Spinner
        String category = !categorySpinner.getSelectedItem().toString().equals("Select a category")
                ? categorySpinner.getSelectedItem().toString()
                : null;

        if (searchContent.isEmpty()) {
            // Ensure the user has entered a search parameter
            Toast.makeText(this, "Please enter a search parameter", Toast.LENGTH_SHORT).show();
        } else {
            // Pass the search content and category to the next activity
            intent.putExtra("searchField", searchContent); // Correct key name
            intent.putExtra("category", category);         // Correct key name
            intent.putExtra("name" , buyerName);
            startActivity(intent); // Start the new activity
        }
    }



    private void fetchCategoriesForSpinner() {
        firebaseRepository.fetchCategories(new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> categoriesList) {
                // Add the default hint item
                categoriesList.add(0, "Select a category");

                // Populate the spinner
                populateSpinner(categoriesList);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error getting documents.", e);
                Toast.makeText(BuyerDashboard.this, "Failed to fetch categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinner(List<String> categoriesList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set item selected listener
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Hint selected
                } else {
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    Toast.makeText(BuyerDashboard.this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
            }
        });
    }
}