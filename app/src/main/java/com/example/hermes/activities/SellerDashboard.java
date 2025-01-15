package com.example.hermes.activities;

import static utils.ActivityUtils.navigateToNextActivity;
import static utils.ActivityUtils.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import data.FirebaseRepository;
import entities.Listing;
import utils.ListingAdapter;

public class SellerDashboard extends AppCompatActivity {

    private ListView listingView;
    private TextView noListingText;
    private ListingAdapter listingAdapter;
    private ArrayList<Listing> listings;
    private ArrayList<Listing> allListings;
    private ArrayList<String> listingNames;

    private FirebaseRepository firebaseRepository;

    private String sellerName;
    private AutoCompleteTextView listingSearch;
    private ImageButton alertButton;
    private TextView alertCounter;
    private ListenerRegistration requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sellerName = getIntent().getStringExtra("name");

        firebaseRepository = new FirebaseRepository();
        listingView = findViewById(R.id.Sellerlistings);
        noListingText = findViewById(R.id.noListingText);
        alertCounter = findViewById(R.id.notificationCounter);
        alertButton = findViewById(R.id.bellIcon);
        listings = new ArrayList<>();
        allListings = new ArrayList<>();
        listingNames = new ArrayList<>();
        listingAdapter = new ListingAdapter(this, listings);
        listingView.setAdapter(listingAdapter);
        listingSearch = findViewById(R.id.listingSearchInput);

        setupItemLongClickListener();
        setupRequestListener();

        fetchListings();
        showNotifications();

        ImageButton addListingButton = findViewById(R.id.addListingButton);

        addListingButton.setOnClickListener(view -> onClickAddListing());
        alertButton.setOnClickListener(view -> goToRequests());
    }

    protected void goToRequests() {
        navigateToNextActivity(this, SellerRequestActivity.class, i -> {
            i.putExtra("sellerName",sellerName);
        });
    }



    private void fetchListings() {
        firebaseRepository.getListingsForSeller(sellerName,
                query -> {
                    allListings.clear();
                    listingNames.clear();

                    for(QueryDocumentSnapshot document: query) {
                        Listing listing = document.toObject(Listing.class);
                        allListings.add(listing);
                        listingNames.add(listing.getName());
                    }

                    listings.clear();
                    listings.addAll(allListings);

                    if(!listings.isEmpty()) {
                        noListingText.setVisibility(View.GONE);
                    } else {
                        noListingText.setVisibility(View.VISIBLE);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SellerDashboard.this, android.R.layout.simple_dropdown_item_1line, listingNames);
                    listingSearch.setAdapter(adapter);
                    listingSearch.setThreshold(1);

                    listingSearch.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedName = (String) parent.getItemAtPosition(position);
                        ArrayList<Listing> filteredList = new ArrayList<>();

                        for (Listing listing: allListings) {
                            if (listing.getName().equals(selectedName)) {
                                filteredList.add(listing);
                                break;
                            }
                        }

                        listings.clear();
                        listings.addAll(filteredList);
                        listingAdapter.notifyDataSetChanged();

                        noListingText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
                    });

                    listingSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if (s.toString().isEmpty()) {

                                listings.clear();
                                listings.addAll(allListings);
                                listingAdapter.notifyDataSetChanged();
                                noListingText.setVisibility(listings.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}


                    });

                    listingAdapter.notifyDataSetChanged();
                },
        e -> {
            showToast(this, "Failed to fetch listings " + e.getMessage());
        });
     }

    protected void setupItemLongClickListener() {
        listingView.setOnItemLongClickListener((parent, view, position, id) -> {
            Listing selectedListing = listings.get(position);
            openListingEditActivity(selectedListing);
            return true;
        });
    };

    private void openListingEditActivity(Listing selectedListing) {
        Intent editIntent = new Intent(SellerDashboard.this, ListingCreationForm.class);

        editIntent.putExtra("isEditing", true);
        editIntent.putExtra("listingId", selectedListing.getListingID());
        editIntent.putExtra("name", selectedListing.getName());
        editIntent.putExtra("category", selectedListing.getCategory());
        editIntent.putExtra("description", selectedListing.getDescription());
        editIntent.putExtra("price", String.valueOf(selectedListing.getPrice()));
        editIntent.putExtra("imageUrl", selectedListing.getImageUrl());
        editIntent.putExtra("startDate", selectedListing.getStartDate());
        editIntent.putExtra("endDate", selectedListing.getEndDate());
        editIntent.putExtra("sellerName", sellerName);

        startActivity(editIntent);
    }

    private void setupRequestListener() {
        if (requestListener != null) {
            requestListener.remove();
        }

        requestListener = firebaseRepository.listenToBuyerRequests(
                sellerName,
                "pending",
                snapshot -> {
                    Log.d("request", String.valueOf(snapshot.size()));
                    int requestCount = snapshot.size();
                    alertCounter.setText(String.valueOf(requestCount));
                    if(!snapshot.isEmpty()) {
                        alertCounter.setVisibility(View.VISIBLE);
                    } else {
                        alertCounter.setVisibility(View.GONE);
                    }
                },
                e-> {
                    showToast(this, "Error listening for buyer request");
                }
        );
    }

    protected void onDestroy() {
        super.onDestroy();
        if(requestListener != null) {
            requestListener.remove();
        }
    }

    protected void showNotifications() {


        firebaseRepository.countRequests(sellerName,
                documents -> {
                    int requestCount = 0;
                    for (QueryDocumentSnapshot request : documents) {
                        requestCount++;
                    }
                    // Update the alertCounter safely with the count
                    alertCounter.setText(String.valueOf(requestCount));

                    if (requestCount != 0) {
                        alertCounter.setVisibility(View.VISIBLE);
                    } else {
                        alertCounter.setVisibility(View.GONE);
                    }
                },
                e -> {
                    // Handle error and notify the user
                    showToast(this, "Failed to fetch requests: " + e.getMessage());
                });
    }

    protected void onClickAddListing() {
        Intent i = new Intent(SellerDashboard.this, ListingCreationForm.class);
        i.putExtra("sellerName", sellerName);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchListings(); // Refreshes listings or any necessary data when the activity resumes
    }

}