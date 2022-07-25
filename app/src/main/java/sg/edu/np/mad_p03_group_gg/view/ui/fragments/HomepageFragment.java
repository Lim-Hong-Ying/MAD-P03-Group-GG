package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sg.edu.np.mad_p03_group_gg.ChatList;
import sg.edu.np.mad_p03_group_gg.Event;
import sg.edu.np.mad_p03_group_gg.EventsPage;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.WeekViewActivity;
import sg.edu.np.mad_p03_group_gg.individual_listing;
import sg.edu.np.mad_p03_group_gg.listingObject;
import sg.edu.np.mad_p03_group_gg.listing_adapter;
import sg.edu.np.mad_p03_group_gg.models.AdBannerImage;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.view.ViewPagerAdapter;
import sg.edu.np.mad_p03_group_gg.view.ui.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Database Constants
    private FirebaseRecyclerAdapter<listingObject, newListingViewholder> firebaseRecyclerAdapter;
    private DatabaseReference databaseReference = FirebaseDatabase.
            getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").
            getReference().child("individual-listing");
    private RecyclerView newListingsRecycler;

    // Initialises event details for meeting planner
    private String name, location, time, date, desc;
    public static String userId;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Only read event details from Firebase once (Meeting Planner)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        if (Event.eventsList.size() == 0){
            readFromFireBase(userId);
        }
        Log.d("EventList", String.valueOf(Event.eventsList.size()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        /**
         * Ad Banner Component
         */
        // Download images from /advertisement which are stored as temp files
        File dir = new File(getContext().getCacheDir(),"advertisement");
        ArrayList<String> filePaths = new ArrayList<>();

        // Check for advertisement directory (for storage of ad images)
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                // Add path to the filePaths array list (later used for RecyclerView Adapter)
                filePaths.add(f.getAbsolutePath());
            }
        }
        else {
            Toast.makeText(getActivity(),
                    "Uh oh, unable to download images.",
                    Toast.LENGTH_SHORT).show();
        }

        ArrayList<AdBannerImage> adBannerImages = new ArrayList<>();

        for (int i = 0; i < filePaths.size(); i++) {
            // Store file path into respective adBannerImage object
            AdBannerImage adBannerImage = new AdBannerImage(filePaths.get(i));
            adBannerImages.add(adBannerImage);
        }

        // Implementation of a Horizontal ViewPager2, able to scroll and snap
        // Refer to https://www.youtube.com/watch?v=O8LA26sAt7Y
        ViewPager2 viewPager2 = view.findViewById(R.id.AdBannerView);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(adBannerImages);

        viewPager2.setAdapter(viewPagerAdapter);

        /**
         * Event Handling
         */
        // Set onClickListeners for Buttons
        CardView listingsCardView = view.findViewById(R.id.listingsButton);
        CardView meetingPlannerCardView = view.findViewById(R.id.meetingPlannerButton);
        ImageView chatButtonView = view.findViewById(R.id.chatPageButton);

        ImageView likedPageButton = view.findViewById(R.id.likedPageButton);
        listingsCardView.setOnClickListener(v -> {
            // When clicked, will bring to listings page which displays all listings
            replaceFragment(new listingFragment());
        });

        chatButtonView.setOnClickListener(v -> {
            Intent chatListIntent = new Intent(getContext(), ChatList.class);
            startActivity(chatListIntent);
        });

        meetingPlannerCardView.setOnClickListener(v -> {
            // When clicked, will bring to meeting planner page which displays all listings
            Intent meetingPlannerIntent = new Intent(this.getContext(), EventsPage.class);
            startActivity(meetingPlannerIntent);
        });

        likedPageButton.setOnClickListener(v -> {
            replaceFragment(new wishListFragment());
        });

        // When user submits search view query, will start search activity
        SearchView searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent searchActivity = new Intent(getContext(), SearchActivity.class);
                // To pass the user's search query to the SearchActivity
                searchActivity.putExtra("query", s);
                startActivity(searchActivity);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // RecyclerView to display last 10 new listings
        newListingsRecycler = view.findViewById(R.id.newListingsRecycler);
        newListingsRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        newListingsRecycler.setFocusable(false);
        newListingsRecycler.setNestedScrollingEnabled(false);
        firebaseNewListing();

        // Inflate the layout for this fragment (finalized the changes, otherwise will not apply)
        return view;
    }

    /**
     * Used to replace an existing fragment in view (similar to startActivity)
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // To apply changes
    }

    /**
     * Read event details of user from Firebase
     * @param userId
     */
    public void readFromFireBase(String userId){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Planner");
        myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    int eventId = Integer.parseInt(snapshot.getKey());
                    name = snapshot.child("name").getValue(String.class);
                    location = snapshot.child("location").getValue(String.class);
                    time = snapshot.child("time").getValue(String.class);
                    date = snapshot.child("date").getValue(String.class);
                    LocalDate dt = LocalDate.parse(date, dtf);
                    desc = snapshot.child("description").getValue(String.class);
                    // Only display current events
                    if (dt.isAfter(LocalDate.now()) || dt.isEqual(LocalDate.now())){
                        Event event = new Event(eventId, name, location, dt, time, desc);
                        Event.eventsList.add(event);
                    }
                    else {
                        continue;
                    }

                    Collections.sort(Event.eventsList, new Comparator<Event>() {
                        @Override
                        public int compare(Event event1, Event event2) {
                            if (event1.getDate() == event2.getDate()){
                                Log.e("Same event Date", "Event date same");
                                try {
                                    return new SimpleDateFormat("hh:mm a").parse(event1.getTime()).compareTo(new SimpleDateFormat("hh:mm a").parse(event2.getTime()));
                                } catch (ParseException e) {
                                    return 0;
                                }
                            }
                            return event1.getDate().compareTo(event2.getDate());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * [Same concept as SearchActivity]
     * Utilize the FirebaseRecyclerAdapter (from com.firebaseui:firebase-ui-database:8.0.1)
     * to populate a RecyclerView of listing objects
     *
     * Since for each individual listing on the database has other attributes that are not used
     * for this scenario, the data snapshot retrieved from the database must be parsed with
     * the correct attributes.
     */
    private void firebaseNewListing() {
        Query query = databaseReference.limitToLast(10); // Recent 10 listings

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
                new FirebaseRecyclerAdapter<listingObject, newListingViewholder>(options) {

                    @NonNull
                    @Override
                    public newListingViewholder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                                     int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.liked_item, parent, false);

                        return new newListingViewholder(view);
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
                    protected void onBindViewHolder(@NonNull newListingViewholder holder,
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

        newListingsRecycler.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * [Same concept as SearchActivity]
     *
     * View Holder for FirebaseRecyclerAdapter
     */
    public class newListingViewholder extends RecyclerView.ViewHolder {
        View searchView;

        public newListingViewholder(@NonNull View itemView) {
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

            // Get username based on seller's ID
            dbReferenceUser.child(model.getSID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    DataSnapshot result = task.getResult();
                    String sellerName = String.valueOf(result.child("name").getValue(String.class));
                    String sellerProfileUrl = String.valueOf(result.child("userprofilepic").getValue(String.class));

                    usernameView.setText(sellerName);
                    Glide.with(getActivity().getApplicationContext()).load(sellerProfileUrl).into(sellerProfilePic);
                }
            });

            listingNameView.setText(model.getTitle());
            listingPriceView.setText("$" + model.getPrice());
            listingItemConditionView.setText(model.getiC());

            // The Glide library is used for easy application of images into their respective views.
            Glide.with(getActivity().getApplicationContext()).load(model.gettURL()).into(listingImageView);
        }
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
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
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