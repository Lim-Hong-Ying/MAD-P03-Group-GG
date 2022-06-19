package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.listingObject;
import sg.edu.np.mad_p03_group_gg.listing_adapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link listingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class listingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public listingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static listingFragment newInstance(String param1, String param2) {
        listingFragment fragment = new listingFragment();
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
        View view = inflater.inflate(R.layout.fragment_listing, container, false);
        ArrayList<listingObject> data = new ArrayList<>();

        viewChanger(view, data);

        retrieveFromFirebase(view, data);
        //data = testlistings(data);
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

                /*switch (mode) {
                    case "grid":
                        editor.putString("view", "card");
                        Log.e("VIEW CHANGED", "CARD");
                        break;

                    case "card":
                        editor.putString("view", "grid");
                        Log.e("VIEW CHANGED", "GRID");
                        break;
                }*/

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
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");
        listing_adapter adapter = recyclerViewStarter(view, data);
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
                    adapter.notifyDataSetChanged();
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
}