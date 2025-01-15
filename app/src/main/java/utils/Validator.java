package utils;
import android.util.Patterns;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Validator {

    // // https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression

    public Validator() {}

    public boolean validateEmailOnSignup(String email) {
        if (email != null) {
            email = email.trim();
            return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;

    }

    public boolean validateUserOnSignup(String username) { return username != null && !username.isEmpty() && username.length() >= 5; }

    public boolean validatePasswordOnSignup(String password) { return password != null && !password.isEmpty() && password.length() >= 8 && password.matches(".*\\d.*"); }

    public boolean validateNewCategoryName(String newName, ArrayList<String[]> existingCategories) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
        
        newName = newName.trim().toLowerCase();
        
        for (String[] category : existingCategories) {
            if (category[1] != null && category[1].trim().toLowerCase().equals(newName)) {
                return false;
            }
        }
        return true;
    }


    //overloaded version to deal with editing 
    public boolean validateNewCategoryName(String newName, ArrayList<String[]> existingCategories, String currentCategoryId) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
        
        newName = newName.trim().toLowerCase();
        
        for (String[] category : existingCategories) {
            // skip comparing with the current category being edited
            if (category[0].equals(currentCategoryId)) {
                continue;
            }
            if (category[1] != null && category[1].trim().toLowerCase().equals(newName)) {
                return false;
            }
        }
        return true;
    }

}