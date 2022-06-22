package sg.edu.np.mad_p03_group_gg.tools;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.listingObject;

/**
 * Various methods to help retrive data from Firebase and improve re-usability
 *
 * TO-DO:
 * 1) Get user data
 * 2) Get chat data
 * 3) Get listings data
 *
 */

public  class FirebaseTools {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private static DatabaseReference databaseReference = database.getReference();

    public static @Nullable String getCurrentAuthenticatedUser() {
        // Retrive user data using the current authenticated session
        String userID;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // User is signed in
            userID = String.valueOf(firebaseUser.getUid());

            return userID;
        } else {
            // No user is signed in
            Log.d("Error:", "Something went wrong, there is no authenticated user.");
            return null;
        }
    }
}