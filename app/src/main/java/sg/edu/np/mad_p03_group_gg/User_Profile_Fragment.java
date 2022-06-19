package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User_Profile_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Profile_Fragment extends Fragment {
    DatabaseReference reference;
    FirebaseAuth auth;
    //StorageReference storageRef = storage.getReference();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public User_Profile_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment User_Profile_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static User_Profile_Fragment newInstance(String param1, String param2) {
        User_Profile_Fragment fragment = new User_Profile_Fragment();
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
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_user__profile_, container, false);

            TextView Email = (TextView) view.findViewById(R.id.user_profile_email);
            TextView Username = (TextView) view.findViewById(R.id.Userprofile_username);
            TextView Phonenumber = (TextView) view.findViewById(R.id.User_Profile_phonenumber);
            ImageView uprofilepic = (ImageView) view.findViewById(R.id.uprofilepic);


            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference databaseReference = database.getReference();



            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Get phone number of current user
                    auth = FirebaseAuth.getInstance();
                    FirebaseUser fbUser = auth.getCurrentUser();
                    String uid = fbUser.getUid();
                    String email = fbUser.getEmail();
                    String phoneNumber = null;
                    String displayName=null;
                    for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                        String foundID = dataSnapshot.child("id").getValue(String.class);
                        if(foundID.equalsIgnoreCase(uid)){
                            phoneNumber = dataSnapshot.child("phonenumber").getValue(String.class);
                            displayName = dataSnapshot.child("name").getValue(String.class);

                            String profilePicUrl = dataSnapshot.child("userprofilepic").
                                    getValue(String.class);
                            User user  = new User(displayName,email,phoneNumber,profilePicUrl);
                            break;
                        }

                    }
                    Email.setText(email);
                    Phonenumber.setText(phoneNumber);
                    Username.setText(displayName);




                    //StorageReference mountainsRef = storageRef.child("mountains.jpg");

                    String profilePicurl = snapshot.child("users").child(uid).child("userprofilepic").getValue(String.class);
                    File dir = new File(getContext().getCacheDir(),"user-images");
                    if (!TextUtils.isEmpty(profilePicurl)) {
                        Picasso.get().load(profilePicurl).into(uprofilepic);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.w("Failed to read value.", error.toException());
                }
            });

            return view;
        }


    }

