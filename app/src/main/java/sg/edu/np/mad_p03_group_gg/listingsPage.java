package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class listingsPage extends AppCompatActivity {

    String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
    DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");
    DatabaseReference db2 = FirebaseDatabase.getInstance(dblink).getReference().child("category");
    String category = null;

    listing_adapter adapter = null;

    ArrayList<listingObject> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings_page);

        //Bundle categoryInfo = getIntent().getExtras();
        //category = categoryInfo.getString("category");
        //db2 = db2.child(category);

        Spinner categorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
        category = categorySpinner.getSelectedItem().toString();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categorySpinner.getSelectedItem().toString();
                db2 = FirebaseDatabase.getInstance(dblink).getReference().child("category").child(category);
                data.clear();
                retrieveFromFirebase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected && data.size() == 0) {
                    Toast.makeText(listingsPage.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(listingsPage.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initialCheckSharedPreferences();
        retrieveFromFirebase(); //Starts main downloading task
        viewChanger(); //Does check for view mode
    }

    private void initialCheckSharedPreferences() {
        SharedPreferences sharedPreferences = listingsPage.this.getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

        if (mode == "") {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("view", "card");
            editor.commit();
        }
    }

    private void viewChanger() { //Changes view for listing
        ToggleButton viewMode = findViewById(R.id.view_mode);

        SharedPreferences sharedPreferences = listingsPage.this.getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

        switch (mode) {
            case "card":
                viewMode.setText("Card view");
                break;

            case "grid":
                viewMode.setText("Grid view");
                break;
        }

        viewMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = listingsPage.this.getSharedPreferences("Cashopee", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (b == false) {
                    editor.putString("view", "card");
                }

                else {
                    editor.putString("view", "grid");
                }

                editor.commit();
                recyclerViewStarter();
            }
        });
    }

    private void retrieveFromFirebase() { //Retrieves data from Firebase
        recyclerViewStarter();
        Log.e("category", category);
        if (category.equals("All listings")) {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot datasnap : snapshot.getChildren()) {
                        String listingid = datasnap.getKey();
                        String titles = datasnap.child("title").getValue(String.class);
                        long thumbnailurlsize = datasnap.child("tURLs").getChildrenCount();
                        ArrayList<String> tURLs = new ArrayList<>();
                        for (int i = 0; i < thumbnailurlsize; i++) {
                            tURLs.add(datasnap.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                        }
                        String sellerid = datasnap.child("sid").getValue(String.class);
                        String itemcondition = datasnap.child("iC").getValue(String.class);
                        String price = datasnap.child("price").getValue(String.class);
                        Boolean reserved = datasnap.child("reserved").getValue(Boolean.class);
                        String timeStamp = datasnap.child("timeStamp").getValue(String.class);

                        listingObject listing = new listingObject(listingid, titles, tURLs, sellerid, itemcondition, price, reserved, timeStamp);
                        data.add(listing);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(listingsPage.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            db2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("snapshot", String.valueOf(snapshot));
                    if (snapshot.exists()) {
                        for (DataSnapshot datasnap : snapshot.getChildren()) {
                            String listingid = datasnap.getKey();
                            if (!listingid.isEmpty()) {
                                DatabaseReference individualListing = db.child(listingid);
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
                                        String sellerid = String.valueOf(result.child("sid").getValue(String.class));
                                        String itemcondition = String.valueOf(result.child("iC").getValue(String.class));
                                        String price = String.valueOf(result.child("price").getValue(String.class));
                                        Boolean reserved = result.child("reserved").getValue(Boolean.class);
                                        String postedTime = result.child("ts").getValue(String.class);

                                        listingObject listing = new listingObject(listingid, titles, tURLs, sellerid, itemcondition, price, reserved, postedTime);
                                        data.add(listing);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }

                    else {
                        Toast.makeText(listingsPage.this, "No listings here!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void recyclerViewStarter() { //Starts recyclerview
        RecyclerView listingRecycler = findViewById(R.id.listing_recycler);
        adapter = new listing_adapter(data);

        SharedPreferences sharedPreferences = listingsPage.this.getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

        switch (mode) {
            case "card":
                LinearLayoutManager cardLayoutManager = new LinearLayoutManager(listingsPage.this);
                listingRecycler.setLayoutManager(cardLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;

            case "grid":
                GridLayoutManager gridLayoutManager = new GridLayoutManager(listingsPage.this, 2);
                listingRecycler.setLayoutManager(gridLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;
        }
    }
}