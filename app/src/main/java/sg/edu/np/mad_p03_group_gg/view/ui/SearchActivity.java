package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.concurrent.RecursiveAction;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.listingObject;

public class SearchActivity extends AppCompatActivity {
    private EditText searchField;
    private ImageButton searchImageButton;
    private RecyclerView searchResultsView;

    private DatabaseReference databaseReference = FirebaseDatabase.
            getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").
            getReference("individual-listing");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchField = findViewById(R.id.searchField);
        searchImageButton = findViewById(R.id.searchImageButton);
        searchResultsView = findViewById(R.id.searchResultsView);

        searchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchField.getText().toString();
                firebaseListingSearch(searchText);
            }
        });
    }

    // View Holder
    public class SearchResultsViewHolder extends RecyclerView.ViewHolder {

        View searchView;

        public SearchResultsViewHolder(@NonNull View itemView) {
            super(itemView);

            searchView = itemView;
        }

        public void setListingResultDetails() {
            TextView usernameView = findViewById(R.id.usernameView  );
            TextView listingNameView = findViewById(R.id.listingNameView);
            TextView listingPriceView = findViewById(R.id.listingPriceView);
            TextView lisingItemConditionView = findViewById(R.id.listingItemConditionView);
            ImageView listingImageView = findViewById(R.id.listingImageView);
        }
    }

    private void firebaseListingSearch(String searchText) {
        Query query = databaseReference.orderByChild("title").startAt(searchText).endAt(searchText
        + "\uf8ff");

        FirebaseRecyclerOptions<listingObject> options =
                new FirebaseRecyclerOptions.Builder<listingObject>()
                        .setQuery(query, listingObject.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter =
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


                    }
                };
    }
}