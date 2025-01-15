package adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hermes.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import data.FirebaseRepository;
import entities.Category;
import entities.Listing;

import com.example.hermes.activities.ListingDetails;
import android.content.Intent;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private FirebaseRepository firebaseRepository;
    private String name;

    public CategoryAdapter(List<Category> categories, FirebaseRepository firebaseRepository , String buyerName) {
        this.categories = categories;
        this.firebaseRepository = firebaseRepository;
        this.name = buyerName != null ? buyerName : "";
    }

    public CategoryAdapter(List<Category> categories, FirebaseRepository firebaseRepository) {
        this(categories, firebaseRepository, ""); 
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryTitle.setText(category.getCategoryName());

        // Fetch listings for the category
        fetchListingsForCategory(category.getCategoryName(), holder);
    }

    private void fetchListingsForCategory(String categoryTitle, CategoryViewHolder holder) {
        firebaseRepository.getListingsForCategory(categoryTitle,
                queryDocumentSnapshots -> {
                    List<Listing> listings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        // Create a Listing object from the document data
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String sellerName = document.getString("sellerName");
                        double price = document.getDouble("price");
                        String startDate = document.getString("startDate");
                        String endDate = document.getString("endDate");
                        String imageUrl = document.getString("imageUrl");
                        String listingID = document.getString("listingID");



                        // Create a new Listing object
                        Listing listing = new Listing(name, categoryTitle, description, sellerName, price, startDate, endDate, imageUrl, listingID);
                        listings.add(listing);
                    }


                        // mk adapter
                        ListingAdapter listingAdapter = new ListingAdapter(listings);

                        // add click listener
                        listingAdapter.setOnListingClickListener(listing -> {
                            Intent intent = new Intent(holder.itemView.getContext(), ListingDetails.class);
                            intent.putExtra("listing", listing);
                            intent.putExtra("name" , name);
                            holder.itemView.getContext().startActivity(intent);
                        });

                        // Setup RecyclerView
                        holder.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                        holder.horizontalRecyclerView.setAdapter(listingAdapter);


                },
                e -> {
                    Log.e("CategoryAdapter", "Error fetching listings: ", e);
                });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView horizontalRecyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_recycler_view);
        }
    }
}