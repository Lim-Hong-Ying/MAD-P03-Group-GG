package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.chat.Chat;

public class userProfile extends AppCompatActivity {

    private String chatKey = "";
    private User seller;
    private User mainUser;

    String uID = null;
    ArrayList<listingObject> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle userid = getIntent().getExtras();
        uID = userid.getString("uid");

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!uID.isEmpty()) {
            retrieveProfileFromFirebase();
        }


        //############## WILLIAM CHAT SECTION ##################

        // Get direct chat button from xml
        Button directChat = findViewById(R.id.send_message);

        // Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference();

        // Get current user id
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = auth.getCurrentUser();
        String currentUserID = fbUser.getUid();

        if (currentUserID.equals(uID)) {
            directChat.setVisibility(View.INVISIBLE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get chatkey
                for (DataSnapshot dataSnapshotCurrentChat : snapshot.child("chat").getChildren()){
                    // Get id number of each user
                    String getUserOne = dataSnapshotCurrentChat.child("user1").getValue(String.class);
                    String getUserTwo = dataSnapshotCurrentChat.child("user2").getValue(String.class);

                    // If id numbers are the same as main user and selected user's id number
                    if((TextUtils.equals(getUserOne,uID) && TextUtils.equals(getUserTwo,currentUserID))
                            || (TextUtils.equals(getUserOne,currentUserID) && TextUtils.equals(getUserTwo, uID))){
                        chatKey = dataSnapshotCurrentChat.getKey();
                    }
                }

                // Create seller and main user Object
                for (DataSnapshot dataSnapshotUser : snapshot.child("users").getChildren()){
                    // If id matches main user ID Create main user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),currentUserID)){
                        mainUser = new User (dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class),currentUserID);
                    }
                    // If id matches main SELLER ID Create seller user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),uID)){
                        seller = new User (dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class)
                                ,dataSnapshotUser.child("phonenumber").getValue(String.class)
                                ,dataSnapshotUser.child("userprofilepic").getValue(String.class)
                                ,uID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read from db
            }
        });

        // Send main user and seller info to chat
        directChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userProfile.this, Chat.class);
                // Send selected user's data to chat activity
                intent.putExtra("name",seller.getName());
                intent.putExtra("profilePic",seller.getUserprofilepic());
                intent.putExtra("chatKey", chatKey);
                intent.putExtra("id", uID);
                // Send main user data to chat activity
                intent.putExtra("mainUser", (Parcelable) mainUser);

                // Add seller to friend list
                databaseReference.child("selectedChatUsers").child(mainUser.getId())
                        .child(uID).setValue("");

                // Start chat activity
                userProfile.this.startActivity(intent);
            }
        });
        // ############# END WILLIAM SECTION ###############
    }

    private void retrieveProfileFromFirebase() {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/"; //Points to Firebase Database
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db); //Retrieves information

        individualdb.getReference().child("users").child(uID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.getResult().exists()) {
                    Toast.makeText(userProfile.this, "Failed to retrieve information. Maybe the user deleted their account.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                else {
                    retrieveFromFirebase();
                    User user = new User();
                    DataSnapshot result = task.getResult();

                    String username = result.child("name").getValue(String.class);
                    String profilePictureURL = result.child("userprofilepic").getValue(String.class);

                    TextView usernameHolder = findViewById(R.id.user_name);
                    ImageView profilepictureHolder = findViewById(R.id.profile_picture);

                    usernameHolder.setText(username);

                    if (!profilePictureURL.isEmpty()) {
                        Picasso.get().load(profilePictureURL).into(profilepictureHolder); //External library to download images
                    }
                }
            }
        });
    }

    private void retrieveFromFirebase() { //Retrieves data from Firebase
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("users").child(uID).child("listings");
        DatabaseReference db2 = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");
        listing_adapter adapter = recyclerViewStarter(data);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnap : snapshot.getChildren()) {
                    String listingid = datasnap.getKey();
                    DatabaseReference individualListing = db2.child(listingid);
                    individualListing.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();
                            if (result.exists()) {
                                String titles = String.valueOf(result.child("title").getValue(String.class));
                                long thumbnailurlsize = result.child("tURLs").getChildrenCount();
                                ArrayList<String> tURLs = new ArrayList<>();
                                for (int i = 0; i < thumbnailurlsize; i++) {
                                    tURLs.add(result.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                                }
                                String sellerid = String.valueOf(result.child("sid").getValue(String.class));
                                String itemcondition = String.valueOf(result.child("iC").getValue(String.class));
                                String price = String.valueOf(result.child("price").getValue(String.class));
                                Boolean reserved = result.child("reserved").getValue(Boolean.class);
                                String postedTime = result.child("ts").getValue(String.class);

                                listingObject listing = new listingObject(listingid, titles, tURLs, sellerid, itemcondition, price, reserved, postedTime);
                                data.add(listing);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(userProfile.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private listing_adapter recyclerViewStarter(ArrayList<listingObject> data) { //Starts recyclerview
        RecyclerView listingRecycler = findViewById(R.id.user_listings);
        listing_adapter adapter = new listing_adapter(data);

        SharedPreferences sharedPreferences = userProfile.this.getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

        switch (mode) {
            case "card":
                LinearLayoutManager cardLayoutManager = new LinearLayoutManager(userProfile.this);
                listingRecycler.setLayoutManager(cardLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;

            case "grid":
                GridLayoutManager gridLayoutManager = new GridLayoutManager(userProfile.this, 2);
                listingRecycler.setLayoutManager(gridLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;
        }

        return adapter;
    }
}