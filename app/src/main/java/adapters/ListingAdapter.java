package adapters;

import static utils.ImageUtil.decodeImageToBitmap;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.ImageView;
import com.example.hermes.R;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import entities.Listing;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.HorizontalViewHolder> {
    private List<Listing> items;
    private OnListingClickListener clickListener;

    public interface OnListingClickListener {
        void onListingClick(Listing listing);
    }

    public void setOnListingClickListener(OnListingClickListener listener) {
        this.clickListener = listener;
    }

    public ListingAdapter(List<Listing> items) {
        this.items = items;
    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        Listing item = items.get(position);
        holder.itemName.setText(item.getName());
        String priceItem = "$" + item.getPrice();
        holder.itemPrice.setText(priceItem);

        // add le click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onListingClick(item);
            }
        });

        String imageByteString = item.getImageUrl();
        if (imageByteString != null && !imageByteString.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(decodeImageToBitmap(imageByteString))
                .error(R.drawable.error_image)
                .into(holder.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;
        TextView itemPrice;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
        }
    }
}