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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        ImageView searchPageCloseButton = findViewById(R.id.paymentMethodCloseButton);

        // Get user query from intent
        Intent fromSearchView = getIntent();
        String searchViewQuery = fromSearchView.getStringExtra("query");
        searchField.setText(searchViewQuery);

        searchPageCloseButton.setOnClickListener(v -> {
            // Termintate the activity
            finish();
        });

        searchResultsView = findViewById(R.id.searchResultsView);
        searchResultsView.setLayoutManager(new LinearLayoutManager(this)); // Vertical scroll
        // When adapter content changed, the height and width of recycler adapter remains fixed
        searchResultsView.setHasFixedSize(true);

        firebaseListingSearch(searchField.getText().toString()); // Get query from searchField
    }

    /**
     * View Holder class for Recycler View
     *
     * setListingResultDetails() will require an object of the listingObject class and thus, set
     * the respective view objects such as TextView and ImageView with their relevant information
     * from the listingObject.
     */
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


            DatabaseReference dbReferenceUser = FirebaseDatabase.
                    getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").
                    getReference().child("users");

            // Get and set the username of the listing seller as well as the seller's profile image
            dbReferenceUser.child(model.getSID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    DataSnapshot result = task.getResult();
                    String sellerName = String.valueOf(result.child("name").getValue(String.class));
                    String sellerProfileUrl = String.valueOf(result.child("userprofilepic").getValue(String.class));

                    usernameView.setText(sellerName);
                    Glide.with(getApplicationContext()).load(sellerProfileUrl).into(sellerProfilePic);

                }
            });

            listingNameView.setText(model.getTitle());
            listingPriceView.setText("$" + model.getPrice());
            listingItemConditionView.setText(model.getiC());
            Glide.with(getApplicationContext()).load(model.gettURL()).into(listingImageView);
        }
    }

    /**
     * Get query, searchText, and uses it to build a FirebaseRecyclerOption object, which is then
     * passed to a FirebaseRecyclerAdapter, as it tells the Adapter what query to perform, and
     * how to parse and format the information retrieved from the database and create a valid
     * result object, which in this case is an object of the listingObject class.
     *
     * Lastly, set the RecyclerView with the configured FirebaseRecyclerAdapter.
     * @param searchText
     */
    private void firebaseListingSearch(String searchText) {
        // A query that filters listings according to the title searched
        // The \uf8ff character is high in the Unicode range and it is located after most regular
        // characters in Unicode which allows query to match all values from starting to ending
        // Source: https://firebase.google.com/docs/database/rest/retrieve-data
        Query query = databaseReference.orderByChild("title").startAt(searchText).endAt(searchText
                + "\uf8ff");

        FirebaseRecyclerOptions<listingObject> options =
                new FirebaseRecyclerOptions.Builder<listingObject>()
                        .setQuery(query, new SnapshotParser<listingObject>() {
                            @NonNull
                            @Override
                            public listingObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                                // What to retrieve and then how to parse it to an object
                                String listingid = snapshot.getKey();
                                String titles = snapshot.child("title").getValue(String.class);
                                String thumbnailurl = snapshot.child("tURL").getValue(String.class);
                                String sellerid = snapshot.child("sid").getValue(String.class);
                                String sellerprofilepicurl = snapshot.child("sppu").getValue(String.class);
                                String itemcondition = snapshot.child("iC").getValue(String.class);
                                String price = snapshot.child("price").getValue(String.class);
                                Boolean reserved = snapshot.child("reserved").getValue(Boolean.class);
                                String TImeStamp = snapshot.child("timeStamp").getValue(String.class);

                                listingObject listing = new listingObject(listingid, titles,
                                        thumbnailurl, sellerid, sellerprofilepicurl,
                                        itemcondition, price, reserved,TImeStamp);

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

                    /**
                     * An event handler (onClickListener) is set to each individual listing card
                     * allowing users to interact and tap on the cards to view more details on the
                     * listing.
                     *
                     * A bundle with the listing ID is passed to an intent which is then used to
                     * start the individual_listing activity, in order for the activity to know
                     * which listing to retrieve from Firebase and display, according to its ID.
                     *
                     * @param holder
                     * @param position
                     * @param model
                     */
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

    /**
     * FirebaseRecyclerAdapter uses event listener to monitor changes to the Firebase query, and
     * startListening() tells the adapter to read at the onStart() part of Android's activity
     * lifecycle, which happens after onCreate() to ensure that the RecyclerView and Adapter is
     * initialised before listening.
     *
     * Source: https://firebaseopensource.com/projects/firebase/firebaseui-android/database/readme/
     */
    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * NOTE: MUST notifyDataSetChanged() when the user navigates out of the activity, such as
         * tapping on a listing card to view more details of a listing and returning back to the
         * SearchActivity. Otherwise there will be inconsistency error.
         *
         * This occurs when the fragment/activity is stopped and all data on the recycler view
         * have been reset, which will raise an inconsistency error.
         */
        firebaseRecyclerAdapter.notifyDataSetChanged();
        firebaseRecyclerAdapter.stopListening();
    }
}