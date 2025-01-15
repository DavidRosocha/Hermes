package data;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entities.Category;
import utils.FirestoreCallback;

public class FirebaseRepository {

    private final FirebaseFirestore firestore;

    public FirebaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void addAccountToFirestore(
            String collection,
            String documentId,
            Object account,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure
    ) {
        firestore.collection(collection)
                .document(documentId)
                .set(account)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void signInAuthentication(
            String collection,
            String email,
            String password,
            OnSuccessListener<? super QuerySnapshot> onSuccess,
            OnFailureListener onFailure
    ) {
        firestore.collection(collection)
                .whereEqualTo("email", email)
                .whereEqualTo("password",password)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void disableAccount(
            String role,
            String email,
            OnSuccessListener<Boolean> onSuccess,
            OnFailureListener onFailure
    ) {
        firestore.collection(role)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result.isEmpty()) {
                            onFailure.onFailure(new Exception("No Documents found"));

                        }

                        for (QueryDocumentSnapshot document: result) {
                            boolean newStatus = toggleAccountState(role,document);
                            onSuccess.onSuccess(newStatus);
                        }
                    } else {
                        onFailure.onFailure(task.getException());
                    }


                });
    }

    private boolean toggleAccountState(String role, QueryDocumentSnapshot document) {
        Boolean isDisabled = document.getBoolean("isDisabled");
        boolean newStatus = (isDisabled == null || !isDisabled);

        firestore.collection(role).document(document.getId())
                .update("isDisabled", newStatus)
                .addOnSuccessListener(aVoid ->
                        System.out.println("Account status updated to " + newStatus))
                .addOnFailureListener(e ->
                        System.err.println("Error updating account status for document ID: " + document.getId() + ", " + e));
        return newStatus;
    }

    public void deleteAccount(String role,
                              String email,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {
        firestore.collection(role)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result.isEmpty()) {
                            onFailure.onFailure(new Exception("no documents were found"));
                        }

                        for (QueryDocumentSnapshot document: result) {
                            firestore.collection(role)
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(onSuccess)
                                    .addOnFailureListener(onFailure);
                        }
                    } else {
                        onFailure.onFailure(task.getException());
                    }
                }).addOnFailureListener(onFailure);
    }

    public void getListingsForSeller(String sellerName,
                                     OnSuccessListener<QuerySnapshot> onSuccess,
                                     OnFailureListener onFailure) {
        firestore.collection("listings")
                .whereEqualTo("sellerName", sellerName)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getListingsForCategory(String category,
                                     OnSuccessListener<QuerySnapshot> onSuccess,
                                     OnFailureListener onFailure) {
        firestore.collection("listings")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void createListing(HashMap<String, Object> newListing,
                              OnSuccessListener<? super DocumentReference> onSuccess,
                              OnFailureListener onFailure) {
        firestore.collection("listings")
                .add(newListing)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);

    }

    public void fetchCategories(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> categoriesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String category = document.getString("categoryName");
                            if (category != null) {
                                categoriesList.add(category);
                            }
                        }
                        onSuccess.onSuccess(categoriesList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        onFailure.onFailure(task.getException()); // Pass the error to the listener
                    }

                });

    }

    public void fetchCategoriesObject(OnSuccessListener<List<Category>> onSuccess, OnFailureListener onFailure) {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category> categoriesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("categoryName");
                            String description = document.getString("description");
                            String id = document.getId();
                            Category category = new Category(categoryName, description, id);
                            if (categoryName != null) {
                                categoriesList.add(category);
                            }
                        }
                        onSuccess.onSuccess(categoriesList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        onFailure.onFailure(task.getException()); // Pass the error to the listener
                    }

                });

    }

    public void editListing(String listingID, HashMap<String, Object> newlisting, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection("listings")
                .whereEqualTo("listingID", listingID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        firestore.collection("listings")
                                .document(documentId)
                                .update(newlisting)
                                .addOnSuccessListener(onSuccess)
                                .addOnFailureListener(onFailure);
                    } else {
                        onFailure.onFailure(new Exception("Listing not found"));
                    }
                }).addOnFailureListener(onFailure);
    }

    public void deleteListing(String listingID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection("listings")
                .whereEqualTo("listingID", listingID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d("DeleteListing", "No documents found for listing: " + listingID);
                            onFailure.onFailure(new Exception("No matching listing found"));
                            return;
                        }

                        for (QueryDocumentSnapshot document: task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(onSuccess)
                                    .addOnFailureListener(onFailure);
                        }
                    } else {
                        Log.w("DeleteListing", "Error getting documents.", task.getException());
                        onFailure.onFailure(task.getException());
                    }
                });
    }

    public void countRequests(String sellerName, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure){
        firestore.collection("buyer_requests")
                .whereEqualTo("sellerName", sellerName)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getBuyerRequests(String sellerName, OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        firestore.collection("buyer_requests")
                .whereEqualTo("sellerName", sellerName)
                .whereEqualTo("status","pending")
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void acceptRequest(String requestID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection("buyer_requests")
                .document(requestID)
                .update("status", "accepted")
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void declineRequest(String requestID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firestore.collection("buyer_requests")
                .document(requestID)
                .update("status", "declined")
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public ListenerRegistration listenToBuyerRequests(
            String sellerName,
            String status,
            OnSuccessListener<QuerySnapshot> onSuccess,
            OnFailureListener onFailure
    ) {
        Query query = firestore.collection("buyer_requests")
                .whereEqualTo("sellerName", sellerName)
                .whereEqualTo("status",status);

        return query.addSnapshotListener((snapshot, error) -> {
            if(error!=null) {
                onFailure.onFailure(error);
                return;
            }
            if(snapshot != null) {
                onSuccess.onSuccess(snapshot);
            }
        });
    }
}
