package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class listings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        ArrayList<listingObject> data = new ArrayList<>();

        ImageButton newListing = findViewById(R.id.add_listing);
        newListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newList = new Intent(view.getContext(), newlisting.class);
                view.getContext().startActivity(newList);
            }
        });

        Switch viewMode = findViewById(R.id.view_mode);
        viewMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = getSharedPreferences("Cashopee", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (b == false) {
                    editor.putString("view", "card");
                    Log.e("VIEW CHANGED", "CARD");
                }

                else {
                    editor.putString("view", "grid");
                    Log.e("VIEW CHANGED", "GRID");
                }

                editor.commit();
                recyclerViewStarter(data);
            }
        });

        retrieveFromFirebase(data);
        //data = testlistings(data);
    }

    private void recyclerViewStarter(ArrayList<listingObject> data) {
        RecyclerView listingRecycler = findViewById(R.id.listing_recycler);
        listing_adapter adapter = new listing_adapter(data);

        SharedPreferences sharedPreferences = getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

        if (mode == "card") {
            LinearLayoutManager listingLayoutMgr = new LinearLayoutManager(this);
            listingRecycler.setLayoutManager(listingLayoutMgr);
            listingRecycler.setItemAnimator(new DefaultItemAnimator());
            listingRecycler.setAdapter(adapter);
        }

        else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            listingRecycler.setLayoutManager(gridLayoutManager);
            listingRecycler.setItemAnimator(new DefaultItemAnimator());
            listingRecycler.setAdapter(adapter);
        }
    }

    private ArrayList<listingObject> testlistings(ArrayList<listingObject> data) {
        String testthumbnail = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/listing-images%2Fi-am-not-a-degenerate-this-is-just-test.jpeg?alt=media&token=d3d97f7a-39ec-4014-ad29-cc9f2bf16368";
        String testpfp = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/user-images%2Fdegeneracy.jpeg?alt=media&token=949a52bf-9c6c-4e27-abfc-3145524e81cd";
        listingObject test1 = new listingObject("1", "test title 1", testthumbnail, "test seller id 1", testpfp, "New", "10", false);
        listingObject test2 = new listingObject("2", "test title 2", testthumbnail, "test seller id 2", testpfp, "Used", "100", true);
        listingObject test3 = new listingObject("3", "test title 3", testthumbnail, "test seller id 3", testpfp, "New", "200", false);
        listingObject test4 = new listingObject("4", "test title 4", testthumbnail, "test seller id 4", testpfp, "Used", "300", false);
        listingObject test5 = new listingObject("5", "test title 5", testthumbnail, "test seller id 5", testpfp, "New", "500", true);

        data.add(test1);
        data.add(test2);
        data.add(test3);
        data.add(test4);
        data.add(test5);

        return data;
    }

    private void retrieveFromFirebase(ArrayList<listingObject> data) {
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnap : snapshot.getChildren()) {
                    String listingid = datasnap.getKey();
                    String titles = datasnap.child("title").getValue(String.class);
                    String thumbnailurl = datasnap.child("tURL").getValue(String.class);
                    String sellerid = datasnap.child("sid").getValue(String.class);
                    String sellerprofilepicurl = datasnap.child("sppu").getValue(String.class);
                    String itemcondition = datasnap.child("iC").getValue(String.class);
                    String price = datasnap.child("price").getValue(String.class);
                    Boolean reserved = datasnap.child("reserved").getValue(Boolean.class);

                    listingObject listing = new listingObject(listingid, titles, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved);
                    data.add(listing);
                    Log.e("listing", String.valueOf(data.size()));
                    recyclerViewStarter(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}