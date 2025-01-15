package com.example.hermes.activities;

import static utils.ActivityUtils.showToast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import data.FirebaseRepository;
import entities.BuyerRequest;
import utils.RequestAdapter;

public class SellerRequestActivity extends AppCompatActivity {

    private ListView requestListView;
    private TextView noRequestsText;
    private RequestAdapter requestAdapter;
    private ArrayList<BuyerRequest> buyerRequests;
    private FirebaseRepository firebaseRepository;
    private String sellerName;
    private ListenerRegistration requestListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestListView = findViewById(R.id.requestListView);
        noRequestsText = findViewById(R.id.noRequestsText);
        buyerRequests = new ArrayList<>();
        firebaseRepository = new FirebaseRepository();
        sellerName = getIntent().getStringExtra("sellerName");

        requestAdapter = new RequestAdapter(this, buyerRequests);
        requestListView.setAdapter(requestAdapter);

        fetchBuyerRequests();
    }

    private void fetchBuyerRequests() {
        firebaseRepository.getBuyerRequests(sellerName, query -> {
            buyerRequests.clear();
            for(QueryDocumentSnapshot doc : query) {
                BuyerRequest request = doc.toObject(BuyerRequest.class);

                buyerRequests.add(request);
            }
            requestAdapter.notifyDataSetChanged();

            noRequestsText.setVisibility(buyerRequests.isEmpty() ? View.VISIBLE : View.GONE);
        },
                e-> {
            showToast(this, e.getMessage());
                });
    }
    public void handleAcceptRequest(BuyerRequest request) {
        // Update Firestore or other data source to reflect acceptance

        firebaseRepository.acceptRequest(request.getId(),
                aVoid -> {
                    buyerRequests.remove(request);
                    requestAdapter.notifyDataSetChanged();
                    showToast(this, "request has been accepted");
                },
                e -> {
                    showToast(this, e.getMessage());
                });
    }

    public void handleDeclineRequest(BuyerRequest request) {
        // Update Firestore or other data source to reflect rejection
        firebaseRepository.declineRequest(request.getId(),
                aVoid -> {
                    buyerRequests.remove(request);
                    requestAdapter.notifyDataSetChanged();
                    showToast(this, "request has been declined");
                },
                e -> {
                    showToast(this, e.getMessage());
                });
    }




}