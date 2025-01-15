package com.example.hermes.activities;

import static utils.ActivityUtils.showToast;
import static utils.ActivityUtils.navigateToNextActivity;
import static utils.AccountUtil.checkAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.UUID;


import android.util.Log;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;

import com.example.hermes.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import data.FirebaseRepository;
import entities.Account;
import entities.Buyer;
import entities.Seller;
import utils.AccountUtil;
import utils.ActivityUtils;
import utils.Validator;

public class SignUpActivity extends AppCompatActivity {

    // finds and stores the ui element via id given in layout xml
    private Spinner spinner;
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button registerButton;
    private TextView signInButton;
    private String role;
    private Validator validator;
    private FirebaseRepository firebaseRepository;
    private final String TAG = "on sign-up";
    private String name;
    private String password;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setListeners();
        populateSpinner();
    }

    protected void initializeComponents() {

        spinner = findViewById(R.id.roles_spinner);
        nameInput = findViewById(R.id.name_input);
        emailInput =findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.register_button);
        signInButton = findViewById(R.id.signInSwitch_button);
        validator = new Validator();
        firebaseRepository = new FirebaseRepository();

    }


    /**
     * Populates the roles for thw dropdown menu
     */
    protected void populateSpinner() {
        // ArrayAdapter returns a view for each item in a collection using string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles_array,
                android.R.layout.simple_spinner_item
        );

        // clarify the layout to use when the roles appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        spinner.setAdapter(adapter);
    }

    /**
     * Registers accounts to firebase
     * @param collection
     * @param id
     * @param account
     * @param nextActivity
     * @param <T>
     */
    private <T> void registerAccount(String collection, String id, Account account, Class<T> nextActivity) {
        firebaseRepository.addAccountToFirestore(
                collection,
                id,
                account,
                success -> {
                    showToast(this," Buyer account created");
                    navigateToNextActivity(this, nextActivity,
                            i -> {
                                i.putExtra("name", account.getName());
                                i.putExtra("email", account.getEmail());
                                i.putExtra("password", account.getPassword());
                                i.putExtra("role", account.getUserRole());
                            });
                },
                error ->  showToast(this,"Error creating account: " + error.getMessage())
        );
    }
    /**
     * Creates accounts
     */
    protected void signUp() {

         String name = nameInput.getText().toString();
         String email = emailInput.getText().toString();
         String password = passwordInput.getText().toString();

         if (checkAdmin(name,password)) {
             showToast(this, "Account Already Exists");

         } else if (role.equals("Renter")) {
            Buyer account = new Buyer(
                    name,
                    email,
                    password
            );
            String buyerID = UUID.randomUUID().toString();
            registerAccount("buyers", buyerID, account, BuyerDashboard.class);
         } else {
            Seller account = new Seller(
                    name,
                    email,
                    password
            );
             String sellerID = UUID.randomUUID().toString();

             registerAccount("sellers", sellerID, account, SellerDashboard.class);

         }
    }

    protected void toSignIn() {
        Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(i);
    }

    protected boolean validateInput() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        if (!validator.validateUserOnSignup(name)) {
            Toast.makeText(this , "invalid user, must be at least 5 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validator.validateEmailOnSignup(email)) {

            Toast.makeText(this, "please enter a valid email" , Toast.LENGTH_SHORT).show();
            return false;

        }

        if (!validator.validatePasswordOnSignup(pass)) {

            Toast.makeText(this,  "password must be 8 characters long and have one digit" , Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    /**
     * sets event listeners for necessary components on the layout
     */
    protected void setListeners() {

        signInButton.setOnClickListener(v -> {
            toSignIn();
        });

       registerButton.setOnClickListener(v -> {
            if (validateInput()) {
                signUp();
            }
        });

        System.out.println(spinner);
        // Event listener for when an item in the dropdown is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // when an item is selected, launch a toast with selected Item (debugging)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                role = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(SignUpActivity.this, "Selected Item: " + role, Toast.LENGTH_SHORT).show();
            }

            // when an item is not selected, do nothing (cannot be removed cause onNothingSelected must be implemented)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }



}