package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.Event;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.loginpage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User_Profile_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Profile_Fragment extends Fragment {
    private int YourRequestCode = 1;
    DatabaseReference reference;


    FirebaseAuth auth;
    private Uri ImageUri;
    private StorageReference storage;
    private User user;
    private DatabaseReference mDataref;
    private StorageTask muploadtask;
    private FirebaseStorage base = FirebaseStorage.getInstance();
    private String Filepath;
    private String profilePicurl = null;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Get storage reference

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__profile_, container, false);

         //Get Views
        TextView Email = (EditText) view.findViewById(R.id.user_profile_email);
        TextView Username = (TextView) view.findViewById(R.id.user_profile_name);
        TextView Phonenumber = (EditText) view.findViewById(R.id.User_Profile_phonenumber);
        ImageView uprofilepic = (ImageView) view.findViewById(R.id.uprofilepic);

        Button log_out = (Button) view.findViewById(R.id.log_out);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDataref = database.getReference();
        storage = FirebaseStorage.getInstance().getReference("user-images");

        retrieveUserAndDisplayImage(view);
        // Get user data from database by in the form of a class.
        mDataref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                auth = FirebaseAuth.getInstance();
                FirebaseUser fbUser = auth.getCurrentUser();
                String uid = fbUser.getUid();
                String email = fbUser.getEmail();

                //Use uid to find the user in database
                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    String foundID = dataSnapshot.child("id").getValue(String.class);
                    if (foundID.equalsIgnoreCase(uid)) {
                        //Get user instance from database and set user

                        user = dataSnapshot.getValue(User.class);
                        user.setId(uid);

                        break;
                    }

                }
                //Get profilepic url and store it in profilePicurl variable


                profilePicurl = user.getUserprofilepic();
                //Set text in the user profile page
                Email.setText(user.getEmail().toString());
                Phonenumber.setText(user.getPhonenumber().toString());
                Username.setText(user.getName().toString());
                Log.e("test","test");




            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.w("Failed to read value.", error.toException());
            }
        });


        //When logged out, will send user back to log-in page and finish the activtiy
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), loginpage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                uprofilepic.setImageURI(result);
                ImageUri = result;

                // write to firebase
                //Prevent user from uploading duplicate images when image is in process of loading
                if (muploadtask != null && muploadtask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {


                    onupload();
                }


            }
        });

        uprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),//Checks for permision to access external storage, if dont have, external storage is request again
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            YourRequestCode);

                }
                else{
                    launcher.launch("image/*");//get image from gallery
                }


            }
        });
//        numberOfevents(user,view);
        return view;

    }
// Get User image from database
    private void retrieveUserAndDisplayImage(View view) {
        String sid = "";
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        CircleImageView userPFP = view.findViewById(R.id.uprofilepic);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            sid = String.valueOf(user.getUid());
        } else {
            // No user is signed in
        }

        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        individualdb.getReference().child("users").child(sid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();
                String spfp = String.valueOf(result.child("userprofilepic").getValue(String.class));

                if(!TextUtils.isEmpty(spfp)) {//if image is present, image is changed to the appropriate image profile picture, else profile image will remain as default
                    new ImageDownloader(userPFP).execute(spfp);

                }
            }
        });
    }
//Get file extension of file
    private String getfileextension(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    //Uploads image to firebase
    private void onupload() {
        //Get Imageview
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.uprofilepic);
        //Get filepath & references
        Filepath = System.currentTimeMillis() + "." + getfileextension(ImageUri);
        StorageReference storageReference = storage.child(Filepath);
        // Convert to bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        //On failure make a toast message to inform user
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Unsuccessful. Please try again", Toast.LENGTH_SHORT).show();

                // Handle unsuccessful uploads
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            //On sucessfull, get image uri and safe to user class and upload it to database
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(getContext(), "Successful.", Toast.LENGTH_SHORT).show();
                                user.setUserprofilepic(uri.toString());
                                mDataref.child("users").child(user.getId()).setValue(user);// upload to database
                            }

                        });
                    }
                }
            }


            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed


            boolean isEmpty(EditText text) {
                CharSequence str = text.getText().toString();
                return TextUtils.isEmpty(str);
            }

            boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
                CharSequence email = text.getText().toString();
                return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
            }

            public boolean checkDataEntered(View v) {  // returns true when all input fields are correct, return false when not correct

                EditText Phonenumber = (EditText) v.findViewById(R.id.User_Profile_phonenumber);
                EditText Email = (EditText) v.findViewById(R.id.user_profile_email);


                if (isEmail(Email) == false) {
                    Email.setError("Enter valid email!");
                    return false;
                }

                if (isEmpty(Phonenumber)) {
                    Phonenumber.setError("Last name is required!");
                    return false;
                }
                return true;
            }


        });
    }
    // Get the number of events
//    private int numberOfevents(User u,View v){
//        //Get views
//        CardView eventcard = (CardView)v.findViewById(R.id.event_card);
//        TextView noevents = (TextView)v.findViewById(R.id.num_ofevents);
//        //Get references
//        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        DatabaseReference myRef = database.getReference("Planner");
//        DatabaseReference user = myRef.child(u.getId());
//        DatabaseReference eventId = user.child("" + id);
//        DatabaseReference userName = eventId.child("name");
//        userName.setValue(name);
//        DatabaseReference userLocation = eventId.child("location");
//        userLocation.setValue(location);
//        DatabaseReference userTime = eventId.child("time");
//        userTime.setValue(time);
//        DatabaseReference userDate = eventId.child("date");
//        userDate.setValue(date);
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                auth = FirebaseAuth.getInstance();
//                FirebaseUser fbUser = auth.getCurrentUser();
//                String uid = fbUser.getUid();
//                String email = fbUser.getEmail();
//
//
//
//                for (DataSnapshot dataSnapshot : snapshot.child(uid).getChildren()) {
//
//                    for(DataSnapshot ds : dataSnapshot.child(id))
//                    {
//
//
//
//                    }
//
//
//
//                        Event events = dataSnapshot.getValue(Event.class);
//
//
//
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//        return 1;
//
//    }
    //Downloads Images
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmap;

        public ImageDownloader(ImageView bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap image = null;
            try {
                InputStream input = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(input);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            bitmap.setImageBitmap(result);
        }
    }
}


