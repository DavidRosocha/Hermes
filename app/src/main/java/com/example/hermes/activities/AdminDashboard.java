package com.example.hermes.activities;

import static utils.ActivityUtils.showToast;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import data.FirebaseRepository;
import entities.Account;
import entities.Admin;
import entities.Category;
import utils.FirestoreCallback;
import utils.Validator;

public class AdminDashboard extends AppCompatActivity {

    private ArrayList<String[]> account = new ArrayList<>();
    private ArrayList<String[]> categories = new ArrayList<>();
    private ArrayList<String[]> listings = new ArrayList<>();
    private int[] completeCalls = {0, 0}; // One for users, one for categories
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseRepository firebaseRepository = new FirebaseRepository();
    private final String TAG = "Admin Login";
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> categoryNames = new ArrayList<>();
    private ArrayList<String> listingNames = new ArrayList<>();
    private Admin admin;
    private Validator validator = new Validator();
    private int buyersCount = 0;

    private Button enableButton;
    private Button disableButton;
    private LinearLayout categoriesButton, usersButton, listingsButton;
    private ListView listingListView;


    /**
     * initializes the activity and sets up the edge-to-edge layout.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // init collections
//        account = new ArrayList<>();
        categories = new ArrayList<>();
//        usernames = new ArrayList<>();
        categoryNames = new ArrayList<>();
        listingNames = new ArrayList<>();
        listings = new ArrayList<>();


        // add loading stuff
        usernames.add("Loading users...");
        categoryNames.add("Loading categories...");
        listingNames.add("Loading listings...");

        completeCalls = new int[]{0, 0};
        db = FirebaseFirestore.getInstance();

        // add the initial adapters w/ the loadings
        ListView userListView = (ListView) findViewById(R.id.admin_user_list);

        userListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, usernames));

        userListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (i >= account.size()) {
                Toast.makeText(this, "Account data not loaded yet.", Toast.LENGTH_SHORT).show();
                return true;
            }
            String[] accountDataReceived = account.get(i);


            String role = "buyers";
            if (i >= buyersCount) {
                role = "sellers";
            }

            showManageAccountDialog(accountDataReceived[1], accountDataReceived[2], role);
            return true;
        });

        categoriesButton = findViewById(R.id.categoriesButton);
        listingsButton = findViewById(R.id.listingsButton);
        usersButton = findViewById(R.id.usersButton);


        ListView categoryListView = findViewById(R.id.admin_category_list);


        categoryListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categoryNames));

        Button addCategoryBtn = findViewById(R.id.add_category_button);
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        ListView listingListView = (ListView) findViewById(R.id.admin_listings_list);

        listingListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listingNames));

        listingListView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            String[] listing = listings.get(i);

            showDeleteListingDialog(listing);
            return true;
        });

        Intent extras = getIntent();
        String name = extras.getStringExtra("name");
        String password = extras.getStringExtra("password");
        admin = new Admin(name, password);

        populateUsers();
        populateCategories();
        populateListings();

        TextView manageTitle = findViewById(R.id.manageTitle);

        categoriesButton.setOnClickListener( v -> {
            categoryListView.setVisibility(View.VISIBLE);
            addCategoryBtn.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            listingListView.setVisibility(View.GONE);
            manageTitle.setText("Manage Categories");

        });

        listingsButton.setOnClickListener( v -> {
            listingListView.setVisibility(View.VISIBLE);
            addCategoryBtn.setVisibility(View.GONE);
            categoryListView.setVisibility(View.GONE);
            userListView.setVisibility(View.GONE);
            manageTitle.setText("Manage Listings");
        });

        usersButton.setOnClickListener( v -> {
            userListView.setVisibility(View.VISIBLE);
            addCategoryBtn.setVisibility(View.GONE);
            categoryListView.setVisibility(View.GONE);
            listingListView.setVisibility(View.GONE);
            manageTitle.setText("Manage Users");
        });

    }

    private void showManageAccountDialog(final String accountName, String accountEmail, String accountRole) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.manage_account_dialog, null);
        dialogBuilder.setView(dialogView);


        final Button buttonEnable = dialogView.findViewById(R.id.buttonEnableAccount);
        final Button buttonDisable = dialogView.findViewById(R.id.buttonDisableAccount);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteAccount);

        dialogBuilder.setTitle(accountName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        db.collection(accountRole)
                .whereEqualTo("email", accountEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()) {
                        Boolean isDisabled = task.getResult().getDocuments().get(0).getBoolean("isDisabled");

                        if(isDisabled != null && isDisabled) {
                            buttonEnable.setVisibility(View.VISIBLE);
                            buttonDisable.setVisibility(View.GONE);
                        } else {
                            buttonDisable.setVisibility(View.VISIBLE);
                            buttonEnable.setVisibility(View.GONE);
                        }
                    }
                });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Once account deletion is successful, dismiss the dialog
//                Log.d("ManageAccountDialog", "Delete button clicked");
                deleteAccount(accountName, accountEmail, accountRole, b::dismiss);
            }
        });

        buttonEnable.setOnClickListener(view -> {
            disableAccount(accountName, accountEmail, accountRole, () -> {
                buttonEnable.setVisibility(View.GONE);
                buttonDisable.setVisibility(View.VISIBLE);
            });

        });

        buttonDisable.setOnClickListener(view -> {
            disableAccount(accountName, accountEmail, accountRole, () -> {
                buttonEnable.setVisibility(View.VISIBLE);
                buttonDisable.setVisibility(View.GONE);
            });
        });

    }

    private void disableAccount(String name, String email, String role, Runnable onComplete) {
        Log.d("DisableAccount", "disabling - Name: " + name + ", Email: " + email + ", Role: " + role);

        firebaseRepository.disableAccount(role, email,
                newStatus -> {
                    String statusMessage = newStatus ? "Account successfully disabled." : "Account successfully enabled.";
                    runOnUiThread(() -> {
                        showToast(this, statusMessage);
                    });
                },
                e -> runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Failed to change account status: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }



    private void deleteAccount(String name, String email, String role, Runnable onComplete) {
        Log.d("DeleteAccount", "Starting deletion for - Name: " + name + ", Email: " + email + ", Role: " + role);

        firebaseRepository.deleteAccount(role, email,
                v -> {
                    showToast(this, name + " account successfully deleted");
                    account.clear();
                    usernames.clear();
                    completeCalls[0] = 0;
                    populateUsers();

                    if(onComplete != null) {
                        onComplete.run();
                    }

                },
                e -> {
                    showToast(this,"Error deleting user account." + e.getMessage());
                });
    }

    /**
     * retrieves user data from a specified firestore collection.
     *
     * @param type     the collection name (e.g., "buyers" or "sellers")
     * @param callback callback to handle the data once retrieved
     */
    private void getUsers(String type, FirestoreCallback callback) {
        db.collection(type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String[]> users = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                if (name == null) name = "Unknown User";

                                String email = document.getString("email");
                                if (email == null) email = "";

                                String[] user = new String[]{
                                        document.getId(),
                                        name,
                                        email
                                };
                                users.add(user);

                                if (type.equals("buyers")) {
                                    buyersCount = users.size();
                                }
                            }
                        } else {
                            Log.d("Admin Dashboard", "Error getting documents: " + task.getException());
                        }
                        callback.onCallback(users);
                    }
                });
    }


    private void getCategories(FirestoreCallback callback) {
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String[]> categoryData = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String[] category = new String[]{
                                        document.getId(),
                                        document.getString("categoryName"),
                                        document.getString("description")
                                };

                                categoryData.add(category);
                            }
                        } else {
                            Log.d(TAG, "Error getting categories: ", task.getException());
                        }
                        callback.onCallback(categoryData);
                    }
                });
    }

    private void getListings(FirestoreCallback callback) {
        db.collection("listings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String[]> listingData = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String[] listing = new String[]{

                                        document.getId(),
                                        document.getString("name"),
                                        document.getString("listingID"),
                                };
                                Log.w("listing", listing[1]);
                                listings.add(listing);
                                listingData.add(listing);
                            }
                        } else {
                            Log.d(TAG, "Error getting categories: ", task.getException());
                        }
                        callback.onCallback(listingData);
                    }
                });
    }

    public void populateUsers() {
        getUsers("buyers", new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<String[]> users) {
                if (users != null) {
                    account.addAll(users);
                }
                completeCalls[0]++;
                checkIfCompleted();
            }
        });

        getUsers("sellers", new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<String[]> users) {
                if (users != null) {
                    account.addAll(users);
                }
                completeCalls[0]++;
                checkIfCompleted();
            }
        });
    }
    public void populateCategories() {
        getCategories(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<String[]> categoryData) {
                categories.clear();
                categoryNames.clear();
                if (categoryData != null) {
                    categories.addAll(categoryData);
                    for (String[] category : categoryData) {
                        categoryNames.add(category[1]); // Add the category name
                    }
                }
                completeCalls[1]++;
                checkIfCompleted();
            }
        });
    }

    public void populateListings() {
        getListings(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<String[]> listings) {
                listingNames.clear();
                if (listings != null) {
                    // Add the fetched listings to the listingNames list (not the 'listings' variable)
                    for (String[] listing : listings) {
                        listingNames.add(listing[1]); // Assuming index 1 has the 'listingName'

                    }

                }
                completeCalls[1]++;
                checkIfCompleted();
                populateListingListView();
            }
        });
    }

    private void checkIfCompleted() {
        if (completeCalls[0] == 2) {
            usernames.clear();  // clear loading
            for (String[] user : account) {
                if (user != null && user[1] != null) {
                    usernames.add(user[1]);
                }
            }
            if (usernames.isEmpty()) {
                usernames.add("No users found");
            }
            populateUserListView();
        }

        if (completeCalls[1] == 2) {
            categoryNames.clear();  // clear loading
            for (String[] category : categories) {
                if (category != null && category[1] != null) {
                    categoryNames.add(category[1]);
                }
            }
            if (categoryNames.isEmpty()) {
                categoryNames.add("No categories found");
            }

            populateListingListView();
            populateCategoryListView();
        }


    }

    private void populateListingListView() {
        ListView listingView = findViewById(R.id.admin_listings_list);
        listingView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listingNames));

    }
    private void populateUserListView() {
        ListView listView = findViewById(R.id.admin_user_list);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, usernames));

        // add listeners back
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (i >= account.size()) {
                Toast.makeText(this, "Account data not loaded yet.", Toast.LENGTH_SHORT).show();
                return true;
            }
            String[] accountDataReceived = account.get(i);
            String role = i >= buyersCount ? "sellers" : "buyers";
            showManageAccountDialog(accountDataReceived[1], accountDataReceived[2], role);
            return true;
        });
    }



    private void populateCategoryListView() {
        ListView categoryListView = findViewById(R.id.admin_category_list);
        ((ArrayAdapter<String>) categoryListView.getAdapter()).notifyDataSetChanged();

        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            if (!categoryNames.get(position).equals("No categories found") &&
                    !categoryNames.get(position).equals("Loading categories...")) {
                showEditCategoryDialog(categories.get(position));
            }
        });
    }

    private void showDeleteListingDialog(String[] listing) {
        Dialog dialog = new Dialog(AdminDashboard.this);

        dialog.setContentView(R.layout.listing_delete_layout);
        Button deleteBtn = dialog.findViewById(R.id.buttonDeleteListing);
        TextView warning = dialog.findViewById(R.id.deletelistingWarning);

        warning.setText("Deleting: " + listing[1]);

        deleteBtn.setOnClickListener(v -> {
            // Delete listing from Firestore
            db.collection("listings")
                    .document(listing[0])
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        for (int i = 0; i < listings.size(); i++) {
                            if (listings.get(i)[0].equals(listing[0])) { // Compare by listing ID
                                listings.remove(i); // Remove from listings array
                                break;
                            }
                        }

                        // On success, remove the listing from the local list and refresh the adapter
                        listingNames.remove(listing[1]); // Remove from listingNames

                        // Assuming you are using an ArrayAdapter:
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) ((ListView) findViewById(R.id.admin_listings_list)).getAdapter();
                        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                        showToast(dialog.getContext(), "Listing has been deleted");
                        dialog.dismiss(); // Close the dialog after deletion
                    })
                    .addOnFailureListener(e -> {
                        showToast(dialog.getContext(), "Listing deletion failed");
                    });
        });

        dialog.show();
    }
    private void showEditCategoryDialog(String[] categoryData) {
        Dialog dialog = new Dialog(AdminDashboard.this);
        dialog.setContentView(R.layout.update_delete_layout);

        EditText nameEdit = dialog.findViewById(R.id.editCategoryName);
        EditText descEdit = dialog.findViewById(R.id.editCategoryDescription);
        Button updateBtn = dialog.findViewById(R.id.buttonUpdateCategory);
        Button deleteBtn = dialog.findViewById(R.id.buttonDeleteCategory);

        nameEdit.setText(categoryData[1]);
        descEdit.setText(categoryData[2]);

        Category category = new Category(
                categoryData[1],
                categoryData[2],
                categoryData[0]
        );

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEdit.getText().toString().trim();
                String newDesc = descEdit.getText().toString().trim();

                if (!newName.isEmpty()) {
                    if (!validator.validateNewCategoryName(newName, categories, categoryData[0])) {
                        Toast.makeText(AdminDashboard.this, "A category with this name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("categories")
                            .document(categoryData[0])
                            .update("categoryName", newName, "description", newDesc)
                            .addOnSuccessListener(unused -> {
                                admin.editCategory(category, newName, newDesc);

                                // update in memorylists
                                int position = categories.indexOf(categoryData);
                                if (position != -1) {
                                    categories.get(position)[1] = newName;
                                    categories.get(position)[2] = newDesc;
                                    categoryNames.set(position, newName);

                                    // update view
                                    ListView categoryListView = findViewById(R.id.admin_category_list);
                                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) categoryListView.getAdapter();
                                    adapter.notifyDataSetChanged();
                                }

                                Toast.makeText(AdminDashboard.this, "Category updated", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AdminDashboard.this, "Update failed", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(AdminDashboard.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("categories")
                        .document(categoryData[0])
                        .delete()
                        .addOnSuccessListener(unused -> {
                            admin.deleteCategory(category);

                            // update in memory lists
                            int position = categories.indexOf(categoryData);
                            if (position != -1) {
                                categories.remove(position);
                                categoryNames.remove(position);

                                // update view
                                ListView categoryListView = findViewById(R.id.admin_category_list);
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) categoryListView.getAdapter();
                                adapter.notifyDataSetChanged();
                            }

                            Toast.makeText(AdminDashboard.this, "Category deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminDashboard.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        dialog.show();
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(AdminDashboard.this);
        dialog.setContentView(R.layout.update_delete_layout); // Reusing the same layout

        // Set the dialog window width and height
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Set the width to match parent
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Keep the height as wrap content
        dialog.getWindow().setAttributes(lp);

        EditText nameEdit = dialog.findViewById(R.id.editCategoryName);
        EditText descEdit = dialog.findViewById(R.id.editCategoryDescription);
        Button updateBtn = dialog.findViewById(R.id.buttonUpdateCategory);
        Button deleteBtn = dialog.findViewById(R.id.buttonDeleteCategory);

        // change the og view to make it look like its for adding
        updateBtn.setText("Add");
        deleteBtn.setVisibility(View.GONE);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEdit.getText().toString().trim();
                String newDesc = descEdit.getText().toString().trim();

                if (!newName.isEmpty()) {
                    if (!validator.validateNewCategoryName(newName, categories, null)) {
                        Toast.makeText(AdminDashboard.this, "A category with this name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // make new object
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("categoryName", newName);
                    categoryData.put("description", newDesc);

                    db.collection("categories")
                            .add(categoryData)
                            .addOnSuccessListener(documentReference -> {
                                // add in memory
                                String[] newCategory = new String[]{
                                        documentReference.getId(),
                                        newName,
                                        newDesc
                                };
                                categories.add(newCategory);
                                categoryNames.add(newName);

                                // update view
                                ListView categoryListView = findViewById(R.id.admin_category_list);
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) categoryListView.getAdapter();
                                adapter.notifyDataSetChanged();

                                Toast.makeText(AdminDashboard.this, "Category added", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AdminDashboard.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(AdminDashboard.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

}