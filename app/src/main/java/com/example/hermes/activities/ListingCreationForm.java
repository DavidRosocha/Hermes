package com.example.hermes.activities;

import static utils.ActivityUtils.showToast;
import static utils.ImageUtil.encodeImageAsByteString;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import data.FirebaseRepository;
import entities.Category;
import entities.Listing;
import utils.DateUtil;
import utils.ImageUtil;

public class ListingCreationForm extends AppCompatActivity {

    private TextView startDateText;
    private TextView endDateText;
    private EditText nameField;
    private EditText priceField;
    private EditText descriptionField;
    private AutoCompleteTextView categorySelector;
    private Button startDateButton;
    private Button endDateButton;
    private Button addPhotosButton;
    private Button createListingButton;
    private Button deleteListingButton;
    private FirebaseFirestore db;
    private Calendar startDateCalendar, endDateCalendar;
    private String startDate;
    private String endDate;
    private ActivityResultLauncher<String> pickImageLauncher;
    private Uri selectedImageUri;
    private String imageByteString = null;
    private boolean isEditing = false;
    private String existingListingId;
    private String sellerName;
    private Intent extras;
    private FirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listing_creation_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseRepository = new FirebaseRepository();
        nameField = findViewById(R.id.listingName);
        priceField = findViewById(R.id.listingPrice);
        descriptionField = findViewById(R.id.listingDescription);
        categorySelector = findViewById(R.id.categorySelector);
        extras = getIntent();
        startDate ="";
        endDate = "";


        fetchCategories();

        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);

        sellerName = extras.getStringExtra("sellerName");

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ImageView imageView = findViewById(R.id.photoPreview);
                        selectedImageUri = uri;
                        imageView.setImageURI(selectedImageUri);

                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                            imageByteString = encodeImageAsByteString(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );



        addPhotosButton = findViewById(R.id.addPhotoButton);
        createListingButton = findViewById(R.id.createListingButton);
        deleteListingButton = findViewById(R.id.deleteListingButton);

        isEditing = extras.getBooleanExtra("isEditing", false);

        if (isEditing) {
            hydrateForm();
        } else {
            deleteListingButton.setVisibility(View.GONE);
        }


        startDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        endDateButton.setOnClickListener(view -> showDatePickerDialog(false));
        addPhotosButton.setOnClickListener(view -> pickImageLauncher.launch("image/*"));
        createListingButton.setOnClickListener(view -> createListing());
        deleteListingButton.setOnClickListener(view -> deleteListing(existingListingId));
    }

    protected void hydrateForm() {
        // hydrate form
        existingListingId = extras.getStringExtra("listingId");
        nameField.setText(extras.getStringExtra("name"));
        descriptionField.setText(extras.getStringExtra("description"));
        priceField.setText(extras.getStringExtra("price"));
        startDateText.setText("Start Date: " + extras.getStringExtra("startDate"));
        endDateText.setText("End Date: " +extras.getStringExtra("endDate"));
        categorySelector.setText(extras.getStringExtra("category"));



        // set image
        imageByteString = extras.getStringExtra("imageUrl");
        if (imageByteString != null && !imageByteString.isEmpty()) {
            ImageView imageView = findViewById(R.id.photoPreview);
            Bitmap bitmap = ImageUtil.decodeImageToBitmap(imageByteString);
            imageView.setImageBitmap(bitmap);
        }
            // change btn text
            createListingButton.setText("Update Listing");
            // make the delete listing button visible
            deleteListingButton.setVisibility(View.VISIBLE);
    }

    protected void createListing() {
        // Check if an image URL has been uploaded before creating the listing
        if (imageByteString == null || imageByteString.isEmpty()) {
            Toast.makeText(this, "Please upload an image before creating the listing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve form field values
        String name = nameField.getText().toString();
        String description = descriptionField.getText().toString();
        String category = categorySelector.getText().toString();
        String price = priceField.getText().toString();


        // assume that all fields are filled
        if (!isEditing) {
            if (name.isEmpty() || category.isEmpty() || description.isEmpty() || price.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String listingID = isEditing ? existingListingId : UUID.randomUUID().toString();

        // Use HashMap to store listing data
        HashMap<String, Object> listingData = new HashMap<String, Object>() {{
            put("name", name);
            put("category", category);
            put("description", description);
            put("sellerName", sellerName);
            put("price", Double.parseDouble(price));
            put("startDate", startDate);
            put("endDate", endDate);
            put("imageUrl", imageByteString);
            put("listingID", listingID);
        }};

       // Listing newListing = new Listing(name, category, description, sellerName, Double.parseDouble(price), calculateRentalDuration(), imageByteString, listingID);

        HashMap<String, Object> cleanData = new HashMap<>(listingData);
        cleanData.remove("imageUrl");
        Log.d("ListingForm", "ListingData being sent (excluding image): " + cleanData.toString());

        if (isEditing) {
            updateListing(listingID, listingData);
        } else {
            firebaseRepository.createListing(listingData,
                    v-> {
                        showToast(this, "Listing created successfully!");
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    },
                    e-> {
                        showToast(this,"Failed to create Listing");
                    });
        }
    }

    protected void updateListing(String listingID, HashMap<String, Object> newListing) {
        firebaseRepository.editListing(listingID, newListing,
                v -> {
                    showToast(this, "Listing updated successfully!");
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                e -> {
                    showToast(this,"Failed to update listing");
                });

    }
    protected void fetchCategories() {
        firebaseRepository.fetchCategories(
                categories -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ListingCreationForm.this,
                            android.R.layout.simple_dropdown_item_1line, categories);
                    categorySelector.setAdapter(adapter);
                    categorySelector.setThreshold(1);
                },
                e -> {
                    showToast(this, "Failed to load categories " + e.getMessage());
                }
        );
    }
    protected void deleteListing(String listingID) {
        Log.d("DeleteListing", "Starting deletion for - " + listingID);

        firebaseRepository.deleteListing(listingID,
                aVoid -> {
                    showToast(this, "Listing deleted successfully");
                    goToSellerDashboard();
                },
                e -> {
                    showToast(this, "Failed to delete listing. Please try again." + e.getMessage());
                    goToSellerDashboard();
                });
    }

    private void goToSellerDashboard() {
        // Finish the current activity to go back to the previous one
        finish();
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar dateToUse = isStartDate ? startDateCalendar : endDateCalendar;

        int year = dateToUse.get(Calendar.YEAR);
        int month = dateToUse.get(Calendar.MONTH);
        int day = dateToUse.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ListingCreationForm.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Use the DateUtil to format the date
                    String formattedDate = DateUtil.formatDate(selectedDate, "dd/MM/yyyy");

                    if (isStartDate) {
                        startDateCalendar.setTime(selectedDate.getTime());
                        startDateText.setText("Start Date: " + formattedDate);
                        startDate = formattedDate;

                        if (!endDate.isEmpty() && endDateCalendar.before(startDateCalendar)) {
                            endDateText.setText("End Date:");
                            endDate = "";
                            Toast.makeText(this, "End date cleared. It must be after the start date.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        endDateCalendar.setTime(selectedDate.getTime());
                        endDateText.setText("End Date: " + formattedDate);
                        endDate = formattedDate;

                        if (!endDateCalendar.after(startDateCalendar)) {
                            endDateText.setText("End Date:");
                            endDate = "";
                            Toast.makeText(this, "End date must be after start date.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
