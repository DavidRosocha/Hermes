package utils;

import static utils.ImageUtil.decodeImageToBitmap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hermes.R;

import java.util.List;

import entities.Listing;

public class ListingAdapter extends ArrayAdapter<Listing> {

    private Context context;
    private List<Listing> listings;

    public ListingAdapter(Context context, List<Listing> listings) {
        super(context, 0, listings);
        this.context = context;
        this.listings = listings;
    }

    /**
     * Returns a view that displays a listing item in the list.
     * This method is responsible for populating the list item with data from the Listing object,
     * including loading the image and setting the name.
     *
     * @param position The position of the item within the data set.
     * @param convertView The recycled view to reuse if possible.
     * @param parent The parent view that this view will be attached to.
     * @return A fully populated view representing a listing item.
     */
    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        // Viewholder pattern to optimize view lookups
        ViewHolder holder;

        // Check if convertView is null (if its not recycled)
        if (convertView == null) {
            // Inflate a new view from the layout file for each list item
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_listing, parent, false);

            // Create and set a new Viewholder to store references to the views
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.listingImage);
            holder.nameTextView = convertView.findViewById(R.id.listingName);
            holder.priceTextView = convertView.findViewById(R.id.listingPrice);

            // Store the holder in the convertView to reuse later
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Listing listing = getItem(position);

        String imageByteString = listing.getImageUrl();

        if (imageByteString != null && !imageByteString.isEmpty()) {

            Glide.with(context).load(decodeImageToBitmap(imageByteString)).into(holder.imageView);
        }
        // Load the image using Glide (or another image loading library)


        // Set the name of the listing
        holder.nameTextView.setText(listing.getName());
        holder.priceTextView.setText(" $" + listing.getPrice());

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView; // ImageView to display the listing image
        TextView nameTextView; // TextView to display the listing name
        TextView priceTextView;
    }
}
