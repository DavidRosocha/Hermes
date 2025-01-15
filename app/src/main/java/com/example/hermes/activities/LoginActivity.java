package com.example.hermes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hermes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


import data.FirebaseRepository;
import entities.Seller;
import static utils.AccountUtil.checkAdmin;
import static utils.ActivityUtils.navigateToNextActivity;
import static utils.ActivityUtils.showToast;

/**
 * loginactivity class manages the user login functionality.
 */
public class LoginActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button signInButton;
    private TextView backButton;
    private EditText email;
    private EditText password;
    public String role;
    private final String adminUser = "admin";
    private final String adminPwd = "XPI76SZUqyCjVxgnUjm0";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseRepository firebaseRepositry = new FirebaseRepository();

    /**
     * called when the activity is first created. sets up the ui components and event listeners.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initializeComponents();
        backToSignUp();
        populateSpinner();
        setListeners();
    }

    protected void initializeComponents() {
        spinner = findViewById(R.id.roles_spinner);
        signInButton = findViewById(R.id.signIn_button);
        backButton = findViewById(R.id.signUpSwitch_button);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
    }


    /**
     * method to switch back to the sign-up screen.
     */
    protected void backToSignUp() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("finish");
                finish();
            }
        });
    }

    /**
     * method to validate the input fields.
     * @return true if the input is valid
     */
    protected boolean validateInput() {
        return true;
    }

    /**
     * handles the sign-in functionality. checks user credentials and starts the appropriate activity.
     */
    protected void signIn() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (checkAdmin(userEmail,userPassword)) {
            handleAdminLogin();
        } else {
            handleUserLogin(userEmail,userPassword);
        }
    }

    /**
     * Handler for admin login
     */
    protected void handleAdminLogin() {
        navigateToNextActivity(this, AdminDashboard.class, i -> {
            i.putExtra("name", "admin");
            i.putExtra("password", "XPI76SZUqyCjVxgnUjm0");
        });
    }

    protected void handleUserLogin(String userEmail, String userPassword) {
        // determines collection using ternary operator, set to buyers if true and set to sellers if false
        String collection = role.equals("Renter") ? "buyers" : "sellers";

        firebaseRepositry.signInAuthentication(
                collection,
                userEmail,
                userPassword,
                queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                            Boolean isDisabled = document.getBoolean("isDisabled");
                            if (isDisabled != null && isDisabled) {
                                Toast.makeText(LoginActivity.this, "Account has been disabled", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(LoginActivity.this, "Successful Sign-In", Toast.LENGTH_SHORT).show();

                            navigateToDashboard(document);
                        }
                    } else {
                        showToast(LoginActivity.this, "Incorrect email or password" );
                    }
                },
                e -> {
                    Toast.makeText(LoginActivity.this, "Error Detected: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    protected void navigateToDashboard(QueryDocumentSnapshot document) {
        Intent intent;
        if("seller".equals(document.getString("userRole"))) {
            intent = new Intent(LoginActivity.this, SellerDashboard.class);
        } else {
            intent = new Intent(LoginActivity.this, BuyerDashboard.class);
        }
        intent.putExtra("name", document.getString("name"));
        intent.putExtra("role", document.getString("userRole"));
        startActivity(intent);
    }

    /**
     * populates the roles in the spinner dropdown menu.
     */
    protected void populateSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * sets the event listeners for the ui components.
     */
    protected void setListeners() {
        signInButton.setOnClickListener(v -> {
            backToSignUp();
        });

        signInButton.setOnClickListener(v -> {
            if (validateInput()) {
                signIn();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                role = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(LoginActivity.this, "Selected Item: " + role, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}