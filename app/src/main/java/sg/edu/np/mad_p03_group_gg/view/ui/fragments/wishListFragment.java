package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.listingObject;
import sg.edu.np.mad_p03_group_gg.listing_adapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link wishListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class wishListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public wishListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment wishListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static wishListFragment newInstance(String param1, String param2) {
        wishListFragment fragment = new wishListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        ArrayList<listingObject> data = new ArrayList<>();

        viewChanger(view, data);

        retrieveFromFirebase(view, data);
        return view;
    }

    private void viewChanger(View view, ArrayList<listingObject> data) {
        ToggleButton viewMode = view.findViewById(R.id.view_mode);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");
        Log.e("READ FROM SP", mode);

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
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cashopee", MODE_PRIVATE);
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
                recyclerViewStarter(view, data);
            }
        });
    }

    private void retrieveFromFirebase(View view, ArrayList<listingObject> data) {
        String uID = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uID = String.valueOf(user.getUid());
        } else {
            // No user is signed in
        }

        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("users").child(uID).child("liked");
        DatabaseReference db2 = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");
        listing_adapter adapter = recyclerViewStarter(view, data);
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
                            String thumbnailurl = String.valueOf(result.child("tURL").getValue(String.class));
                            String sellerid = String.valueOf(result.child("sid").getValue(String.class));
                            String sellerprofilepicurl = String.valueOf(result.child("sppu").getValue(String.class));
                            String itemcondition = String.valueOf(result.child("iC").getValue(String.class));
                            String price = String.valueOf(result.child("price").getValue(String.class));
                            Boolean reserved = result.child("reserved").getValue(Boolean.class);

                            listingObject listing = new listingObject(listingid, titles, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved);
                            data.add(listing);
                            Log.e("listing", String.valueOf(data.size()));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private listing_adapter recyclerViewStarter(View view, ArrayList<listingObject> data) {
        RecyclerView listingRecycler = view.findViewById(R.id.listing_recycler);
        listing_adapter adapter = new listing_adapter(data);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");
        Log.e("READ FROM SP", mode);

        switch (mode) {
            case "card":
                LinearLayoutManager cardLayoutManager = new LinearLayoutManager(view.getContext());
                listingRecycler.setLayoutManager(cardLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;

            case "grid":
                GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
                listingRecycler.setLayoutManager(gridLayoutManager);
                listingRecycler.setItemAnimator(new DefaultItemAnimator());
                listingRecycler.setAdapter(adapter);
                break;
        }

        return adapter;
    }
}