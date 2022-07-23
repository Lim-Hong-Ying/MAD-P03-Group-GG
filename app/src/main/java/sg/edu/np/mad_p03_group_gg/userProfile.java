package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class userProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle userid = getIntent().getExtras();
        String uid = userid.getString("uid");
        ArrayList<listingObject> data = new ArrayList<>();

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        retrieveProfileFromFirebase(uid);
        retrieveFromFirebase(data, uid);
    }

    private void retrieveProfileFromFirebase(String uid) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/"; //Points to Firebase Database
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db); //Retrieves information

        individualdb.getReference().child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(userProfile.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                }
                else {
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

    private void retrieveFromFirebase(ArrayList<listingObject> data, String uID) { //Retrieves data from Firebase
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
                            String titles = String.valueOf(result.child("title").getValue(String.class));
                            long thumbnailurlsize = result.child("tURLs").getChildrenCount();
                            ArrayList<String> tURLs = new ArrayList<>();
                            for (int i = 0; i < thumbnailurlsize; i++) {
                                tURLs.add(result.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                            }
                            //String thumbnailurl = String.valueOf(result.child("tURL").getValue(String.class));
                            String sellerid = String.valueOf(result.child("sid").getValue(String.class));
                            String sellerprofilepicurl = String.valueOf(result.child("sppu").getValue(String.class));
                            String itemcondition = String.valueOf(result.child("iC").getValue(String.class));
                            String price = String.valueOf(result.child("price").getValue(String.class));
                            Boolean reserved = result.child("reserved").getValue(Boolean.class);

                            listingObject listing = new listingObject(listingid, titles, tURLs, sellerid, itemcondition, price, reserved);
                            data.add(listing);
                            adapter.notifyDataSetChanged();
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