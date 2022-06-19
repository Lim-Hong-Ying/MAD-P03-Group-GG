package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.ChatList;
import sg.edu.np.mad_p03_group_gg.Event;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.WeekViewActivity;
import sg.edu.np.mad_p03_group_gg.individual_listing;
import sg.edu.np.mad_p03_group_gg.listingObject;
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
    private String name, location, time, date;
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static String userId = user.getUid();

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
        if (Event.eventsList.size() == 0){
            readFromFireBase(userId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        // Download images from /advertisement which are stored as temp files
        File dir = new File(getContext().getCacheDir(),"advertisement");
        ArrayList<String> filePaths = new ArrayList<>();

        // Check for advertisement directory (for storage of ad images)
        if (dir.exists()) {
            if (dir.listFiles().length == 0) {
                // If directory exists, but empty, will download files
                downloadFiles("advertisement");
            }
            for (File f : dir.listFiles()) {
                // Add path to the filePaths array list (later used for RecyclerView Adapter)
                filePaths.add(f.getAbsolutePath());
            }
        }
        else {
            // If directory specified does not exist, call downloadFiles() which will also
            // create a new directory
            downloadFiles("advertisement");
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
            Intent meetingPlannerIntent = new Intent(this.getContext(), WeekViewActivity.class);
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
        //newListingsRecycler.setHasFixedSize(true);
        newListingsRecycler.setFocusable(false);
        newListingsRecycler.setNestedScrollingEnabled(false);
        firebaseNewListing();

        String userID = FirebaseTools.getCurrentAuthenticatedUser();
        Log.d("Current Authenticated User in Liked Page", userID);

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
     * Download ALL files in a certain directory (specified with the parameter, folder) from
     * the Firebase Storage and store them into a folder located within the cache (named after
     * the parameter)
     *
     * eg. downloadFiles("advertisement"), expect to find your files in the cache directory of
     * /advertisement
     *
     * @param folder
     */
    private void downloadFiles(String folder) {
        // Init Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cashoppe-179d4.appspot.com/");

        // Create a storage reference, basically a pointer to a file in the Firebase cloud
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference filesRef = storageRef.child(folder);

        // List all images in /<folder> eg. can be /advertisement
        filesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference fileRef : listResult.getItems()) {
                            // TODO: Download the file using its reference (fileRef)

                            // Download files from the folder, eg. images from /advertisement
                            try {
                                File outputDirectory = new File(getContext().getCacheDir(), folder);
                                if (!outputDirectory.exists()) {
                                    outputDirectory.mkdirs();
                                }

                                File localFile = File.createTempFile("advert", ".jpg", outputDirectory);
                                fileRef.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                            catch (Exception e) {
                                Log.e("Unable to download image", String.valueOf(e));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        Toast.makeText(getActivity(),
                                "An Error Occured: Unable to List Items from Firebase",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
                    Event event = new Event(eventId, name, location, dt, time);
                    Event.eventsList.add(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    /**
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

            usernameView.setText(model.getSID());
            listingNameView.setText(model.getTitle());
            listingPriceView.setText(model.getPrice());
            listingItemConditionView.setText(model.getiC());

            // The Glide library is used for easy application of images into their respective views.
            Glide.with(getActivity().getApplicationContext()).load(model.getSPPU()).into(sellerProfilePic);
            Glide.with(getActivity().getApplicationContext()).load(model.gettURL()).into(listingImageView);
        }
    }

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