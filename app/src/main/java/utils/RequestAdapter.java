package utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hermes.R;
import com.example.hermes.activities.SellerRequestActivity;

import java.util.List;

import entities.BuyerRequest;

public class RequestAdapter extends ArrayAdapter<BuyerRequest> {
    private Context context;

    public RequestAdapter(Context context, List<BuyerRequest> requests) {
        super(context, 0, requests);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_buyer_request, parent, false);
        }

        BuyerRequest request = getItem(position);

        TextView buyerRequestText = convertView.findViewById(R.id.buyerRequestText);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);
        Button declineButton = convertView.findViewById(R.id.declineButton);

        buyerRequestText.setText(request.toString());

        acceptButton.setOnClickListener(v -> {
            if (context instanceof SellerRequestActivity) {

                ((SellerRequestActivity) context).handleAcceptRequest(request);
            }
        });

        declineButton.setOnClickListener(v -> {
            if (context instanceof SellerRequestActivity) {
                ((SellerRequestActivity) context).handleDeclineRequest(request);
            }
        });

        return convertView;
    }
}

