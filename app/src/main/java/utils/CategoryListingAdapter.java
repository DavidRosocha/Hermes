package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ListAdapter;

import com.example.hermes.R;
import com.example.hermes.activities.ListItem;

import java.util.ArrayList;
import java.util.List;

import entities.Listing;
import entities.Category;

public class CategoryListingAdapter extends BaseAdapter {
    private Context context;
    private List<ListItem> items;
    private ListingAdapter listingAdapter;
    private List<Listing> listingsOnly;

    public CategoryListingAdapter(Context context, List<ListItem> items) {
        this.context = context;
        this.items = items;

        // Extract listings from items to pass to ListingAdapter
        listingsOnly = new ArrayList<>();
        for (ListItem item : items) {
            if (item.getType() == ListItem.TYPE_LISTING) {
                listingsOnly.add(item.getListing());
            }
        }

        // Initialize ListingAdapter with the extracted listings
        listingAdapter = new ListingAdapter(context, listingsOnly);
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Two types: category header and listing item
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Keep track of the listing position for ListingAdapter
    private int listingPosition = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == ListItem.TYPE_CATEGORY) {
            // Handle category header
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category_header, parent, false);
            }
            TextView categoryNameTextView = convertView.findViewById(R.id.categoryNameTextView);
            Category category = items.get(position).getCategory();
            categoryNameTextView.setText(category.getCategoryName());
            return convertView;
        } else {
            // Handle listing item by delegating to ListingAdapter
            // Get the corresponding listing position in listingsOnly
            Listing listing = items.get(position).getListing();
            int listingIndex = listingsOnly.indexOf(listing);

            // Delegate to ListingAdapter's getView
            View listingView = listingAdapter.getView(listingIndex, convertView, parent);
            return listingView;
        }
    }
}
