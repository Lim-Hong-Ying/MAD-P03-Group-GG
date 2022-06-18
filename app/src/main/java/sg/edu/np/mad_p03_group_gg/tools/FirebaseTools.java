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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Optional;

import sg.edu.np.mad_p03_group_gg.User;

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

    public static User getIndividualUser() {
        // Retrive user data using the current authenticated session
        final User[] mainUser = new User[1];

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReference = database.getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseAuth finalAuth = auth;
        Log.d("current user in FirebaseTools", finalAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get key id of current user
                FirebaseUser fbUser = finalAuth.getCurrentUser();
                String userID = fbUser.getUid();
                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    String foundID = dataSnapshot.child("id").getValue(String.class);
                    if(foundID.equals(userID)){
                        String phoneNumber = dataSnapshot.child("phonenumber").getValue(String.class);
                        String displayName = dataSnapshot.child("name").getValue(String.class);
                        String profilePic = dataSnapshot.child("image").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        mainUser[0] = new User(displayName, email, phoneNumber, profilePic, foundID);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Database Error", error.getDetails());
            }
        });

        return mainUser[0];
    }

    public static void dateTimeConversion() {

    }

}