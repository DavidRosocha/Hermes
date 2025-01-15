package com.example.hermes.activities;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.hermes.R;
import entities.Listing;
import entities.BuyerRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.util.UUID;


import utils.ImageUtil;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;


public class ListingDetails extends AppCompatActivity {
    private TextView selectedDatesText;  
    private TextView priceText;
    private TextView sellerText;
    private TextView listingNameText;
    private ImageView listingImg;
    private TextView descriptionText;
    private Button startDateButton;
    private Button endDateButton;
    private Button makeOfferButton;
    
    private Calendar selectedStartDate = null;
    private Calendar selectedEndDate = null;
    private Calendar listingStartDate;
    private Calendar listingEndDate;
    private Listing listing;
    private FirebaseFirestore db;

    private String buyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        EdgeToEdge.enable(this);


        // Get listing from intent
        listing = (Listing) getIntent().getSerializableExtra("listing");
        if (listing == null) {
            Toast.makeText(this, "Error loading listing details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buyerName = getIntent().getStringExtra("name");

    
        // init views
        selectedDatesText = findViewById(R.id.selected_dates);
        priceText = findViewById(R.id.price);
        sellerText = findViewById(R.id.seller_name);
        descriptionText = findViewById(R.id.description);
        startDateButton = findViewById(R.id.start_date_button);
        endDateButton = findViewById(R.id.end_date_button);
        makeOfferButton = findViewById(R.id.make_offer_button);
        listingImg = findViewById(R.id.listing_image);
        listingNameText = findViewById(R.id.listing_name);

        
        // Set listing details to views
        priceText.setText(String.format("$%.2f", listing.getPrice()));
        sellerText.setText("Seller: " + listing.getSellerName());
        descriptionText.setText("Description: " + listing.getDescription());
        listingNameText.setText(listing.getName());

        String imageByteString =  listing.getImageUrl();
        if (imageByteString != null && !imageByteString.isEmpty()) {
            Bitmap bitmap = ImageUtil.decodeImageToBitmap(imageByteString);
            listingImg.setImageBitmap(bitmap);
        }

        
        // parse dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            listingStartDate = Calendar.getInstance();
            listingEndDate = Calendar.getInstance();
            listingStartDate.setTime(sdf.parse(listing.getStartDate()));
            listingEndDate.setTime(sdf.parse(listing.getEndDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));
        
        makeOfferButton.setOnClickListener(v -> {
            if (selectedStartDate == null || selectedEndDate == null) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
                return;
            }
            
            db = FirebaseFirestore.getInstance();
            Date time = new Date();
            Timestamp timestamp = new Timestamp(time);
            BuyerRequest req = new BuyerRequest(UUID.randomUUID().toString() , buyerName , listing.getSellerName() , listing.getListingID() , listing.getName() , "meow" , timestamp , "pending");
            
            db.collection("buyer_requests")
                .document(req.getId()) 
                .set(req)
                .addOnSuccessListener(aVoid -> {
                    // success :)
                    Toast.makeText(this, "offer made!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "BuyerRequest successfully written!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    // fail :(
                    Toast.makeText(this, "server error", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error writing BuyerRequest", e);
                });



        });
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // validate
                if (isStartDate) {
                    if (selectedEndDate != null && selectedDate.after(selectedEndDate)) {
                        Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedStartDate = selectedDate;
                } else {
                    if (selectedStartDate != null && selectedDate.before(selectedStartDate)) {
                        Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedEndDate = selectedDate;
                }

                updateSelectedDatesText();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // set min max & validate
        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(listingStartDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(listingEndDate.getTimeInMillis());
        } else {
            if (selectedStartDate != null) {
                datePickerDialog.getDatePicker().setMinDate(selectedStartDate.getTimeInMillis());
            } else {
                datePickerDialog.getDatePicker().setMinDate(listingStartDate.getTimeInMillis());
            }
            datePickerDialog.getDatePicker().setMaxDate(listingEndDate.getTimeInMillis());
        }
        
        datePickerDialog.show();
    }

    private void updateSelectedDatesText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateText = "Selected Dates:\n";
        
        if (selectedStartDate != null) {
            dateText += "Start: " + sdf.format(selectedStartDate.getTime()) + "\n";
        }
        if (selectedEndDate != null) {
            dateText += "End: " + sdf.format(selectedEndDate.getTime());
        }
        
        selectedDatesText.setText(dateText);
    }
}