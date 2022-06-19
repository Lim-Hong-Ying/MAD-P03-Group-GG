package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.individual_listing;
import sg.edu.np.mad_p03_group_gg.listingObject;

public class SearchActivity extends AppCompatActivity {
    private EditText searchField;
    private RecyclerView searchResultsView;
    private FirebaseRecyclerAdapter<listingObject, SearchResultsViewHolder> firebaseRecyclerAdapter;

    private DatabaseReference databaseReference = FirebaseDatabase.
            getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").
            getReference().child("individual-listing");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set a toolbar which allows user to close the activity
        getSupportActionBar().setDisplayShowTitleEnabled(false); // To disable appname title

        searchField = findViewById(R.id.searchField);
        ImageView searchPageCloseButton = findViewById(R.id.searchPageCloseButton);

        // Get user query from intent
        Intent fromSearchView = getIntent();
        String searchViewQuery = fromSearchView.getStringExtra("query");
        searchField.setText(searchViewQuery);

        searchPageCloseButton.setOnClickListener(v -> {
            // Termintate the LikedPage activity
            finish();
        });

        searchResultsView = findViewById(R.id.searchResultsView);
        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsView.setHasFixedSize(true);

        firebaseListingSearch(searchField.getText().toString());
    }

    // View Holder
    public class SearchResultsViewHolder extends RecyclerView.ViewHolder {
        View searchView;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            searchView = itemView;
        }

        public void setListingResultDetails(listingObject model) {

            TextView usernameView = searchView.findViewById(R.id.usernameView  );
            TextView listingNameView = searchView.findViewById(R.id.listingNameView);
            TextView listingPriceView = searchView.findViewById(R.id.listingPriceView);
            TextView listingItemConditionView = searchView.findViewById(R.id.listingItemConditionView);
            ShapeableImageView sellerProfilePic = searchView.findViewById(R.id.profilePictureView);
            ImageView listingImageView = searchView.findViewById(R.id.listingImageView);

            usernameView.setText(model.getSID());
            listingNameView.setText(model.getTitle());
            listingPriceView.setText(model.getPrice());
            listingItemConditionView.setText(model.getiC());
            Glide.with(getApplicationContext()).load(model.getSPPU()).into(sellerProfilePic);
            Glide.with(getApplicationContext()).load(model.gettURL()).into(listingImageView);
        }
    }

    private void firebaseListingSearch(String searchText) {

        Query query = databaseReference.orderByChild("title").startAt(searchText).endAt(searchText
                + "\uf8ff");

        FirebaseRecyclerOptions<listingObject> options =
                new FirebaseRecyclerOptions.Builder<listingObject>()
                        .setQuery(query, new SnapshotParser<listingObject>() {
                            @NonNull
                            @Override
                            public listingObject parseSnapshot(@NonNull DataSnapshot snapshot) {

                                String listingid = snapshot.getKey();
                                String titles = snapshot.child("title").getValue(String.class);
                                String thumbnailurl = snapshot.child("tURL").getValue(String.class);
                                String sellerid = snapshot.child("sid").getValue(String.class);
                                String sellerprofilepicurl = snapshot.child("sppu").getValue(String.class);
                                String itemcondition = snapshot.child("iC").getValue(String.class);
                                String price = snapshot.child("price").getValue(String.class);
                                Boolean reserved = snapshot.child("reserved").getValue(Boolean.class);

                                listingObject listing = new listingObject(listingid, titles,
                                        thumbnailurl, sellerid, sellerprofilepicurl,
                                        itemcondition, price, reserved);

                                return listing;
                            }
                        })
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<listingObject, SearchResultsViewHolder>(options) {

                    @NonNull
                    @Override
                    public SearchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                      int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.liked_item, parent, false);

                        return new SearchResultsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull SearchResultsViewHolder holder,
                                                    int position, @NonNull listingObject model) {
                        holder.setListingResultDetails(model);
                        holder.searchView.findViewById(R.id.parentCardView).setOnClickListener(view -> {
                            Bundle listinginfo = new Bundle();
                            listinginfo.putString("lID", model.getlID());
                            Intent individuallisting = new Intent(view.getContext(), individual_listing.class);
                            individuallisting.putExtras(listinginfo);
                            view.getContext().startActivity(individuallisting);
                        });
                    }
                };
        searchResultsView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * MUST: Otherwise there will be inconsistency error.
         *
         * This occurs when the fragment/activity is closed and all data on the recycler view
         * have been reset, which will raise an inconsistency error.
         */
        firebaseRecyclerAdapter.notifyDataSetChanged();
        firebaseRecyclerAdapter.stopListening();
    }
}