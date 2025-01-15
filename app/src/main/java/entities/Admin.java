package entities;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hermes.activities.LoginActivity;
import com.example.hermes.activities.PostLoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an admin user in the system, extending the Account class and implementing category management functionality.
 */
public class Admin extends Account implements CategoryManager {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Admin instance";



//    /**
//     * A list to store the categories managed by the admin.
//     */
//    private List<Category> categories;

    private String addCategoryTAG = "Adding Category";

    /**
     * Constructs a new admin with all details.
     *
     * @param name                 the name of the admin
     * @param email                the email address of the admin
     * @param password             the password for the admin account
     * @param role                 the role of the admin in the system
     * @param phoneNumber          the phone number of the admin
     * @param profilePicture       the profile picture of the admin
     * @param accountCreationDate  the date when the admin account was created
     * @param age                  the age of the admin
     * @param residenceCity        the city where the admin resides
     * @param adminID              the unique identifier for the admin
     */
    public Admin(String name, String email, String password, String role, String phoneNumber,
                 byte[] profilePicture, long accountCreationDate, int age, String residenceCity, String adminID) {
        super(name, email, password, role, phoneNumber, profilePicture, accountCreationDate, age, residenceCity, adminID);

//        this.categories = new ArrayList<>();

    }

    public Admin(String name, String password) {
        super(name, "admin",password,  "admin");
//        this.categories = new ArrayList<>();

    }

    /**
     * Removes a user from the system.
     *
     * @param account the account to be removed
     */
    public void removeUser(Account account) {
        // Implementation for removing a user
        System.out.println("User removed: " + account.getName());
    }

    /**
     * Disables a user in the system.
     *
     * @param account the account to be disabled
     */
    public void disableUser(Account account) {
        // Implementation for disabling a user
        System.out.println("User disabled: " + account.getName());
    }

    /**
     * Removes a listing from the system.
     *
     * @param listing the listing to be removed
     */
    public void removeListing(Listing listing) {
        // Implementation for removing a listing
        System.out.println("Listing removed: " + listing.getName());
    }

    // Implementing methods from CategoryManager
    /**
     * Creates a new category.
     *
     * @param name        the name of the new category
     * @param description the description of the new category
     */
    @Override
    public void createCategory(String name, String description) {
        Category newCategory = new Category(name, description, "");  // ID will be set by Firestore

        db.collection("categories")
            .add(newCategory) 
            .addOnSuccessListener(documentReference -> {
                // update cateogry with firestore id
                documentReference.update("categoryId", documentReference.getId());
                Log.d(TAG, "Category created with ID: " + documentReference.getId());
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error creating category", e);
            });
    }


    /**
     * Edits an existing category.
     *
     * @param category       the category to be edited
     * @param newName        the new name for the category
     * @param newDescription the new description for the category
     */
    @Override
    public void editCategory(Category category, String newName, String newDescription) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("categoryName", newName);
        updates.put("description", newDescription);

        db.collection("categories")
            .document(category.getCategoryId())
            .update(updates)
            .addOnSuccessListener(unused -> {
                Log.d(TAG, "Category updated successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating category", e);
            });
    }

    /**
     * Deletes a category.
     *
     * @param category the category to be deleted
     */
    @Override
    public void deleteCategory(Category category) {
        db.collection("categories")
            .document(category.getCategoryId())
            .delete()
            .addOnSuccessListener(unused -> {
                Log.d(TAG, "Category deleted successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting category", e);
            });
    }

    /**
     * Lists all categories.
     *
     * @return a list of all categories
     */
    @Override
    public List<Category> listCategories() {
        return new ArrayList<>();
    }
}
