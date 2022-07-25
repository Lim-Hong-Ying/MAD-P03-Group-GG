package sg.edu.np.mad_p03_group_gg.view.ui.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.Event;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.WeekViewActivity;
import sg.edu.np.mad_p03_group_gg.deleteaccount;
import sg.edu.np.mad_p03_group_gg.listingObject;
import sg.edu.np.mad_p03_group_gg.loginpage;
import sg.edu.np.mad_p03_group_gg.changeaccdetails;
import sg.edu.np.mad_p03_group_gg.tools.StripeUtils;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.ConnectStripeCallback;
import sg.edu.np.mad_p03_group_gg.view.ui.CheckoutActivity;
import sg.edu.np.mad_p03_group_gg.view.ui.StripeDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link User_Profile_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class User_Profile_Fragment extends Fragment {
    private int YourRequestCode = 1;
    DatabaseReference reference;
    // Set variables

    FirebaseAuth auth;
    private Uri ImageUri;
    private StorageReference storage;
    private User user;
    private DatabaseReference mDataref;
    private StorageTask muploadtask;
    private FirebaseStorage base = FirebaseStorage.getInstance();
    private String Filepath;
    private String profilePicurl = null;
    private FirebaseUser fbUser;
    private List<listingObject> llist ;
    private int nooflisiting;
    private String userId;

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
        getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final StripeDialog stripeDialog = new StripeDialog(this.getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__profile_, container, false);


         //Get Views
        TextView Email = (TextView) view.findViewById(R.id.user_profile_email);
        TextView Username = (TextView) view.findViewById(R.id.user_profile_name);
        TextView Phonenumber = (TextView) view.findViewById(R.id.User_Profile_phonenumber);
        ImageView uprofilepic = (ImageView) view.findViewById(R.id.uprofilepic);

        Button log_out = (Button) view.findViewById(R.id.log_out);
        Button delete =  (Button)view.findViewById(R.id.Delete_Account);
        Button EditProfilebtn = (Button)view.findViewById(R.id.editprofilebutton);

        //Get References
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDataref = database.getReference();
        storage = FirebaseStorage.getInstance().getReference("user-images");
        //Retrive and display profile picture
        retrieveUserAndDisplayImage(view);


        // Get user data from database by in the form of a class.
        mDataref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get authenticated user from an instance
                auth = FirebaseAuth.getInstance();
                fbUser = auth.getCurrentUser();
                //Get parameters
                String uid = fbUser.getUid();
                String email = fbUser.getEmail();

                //Use uid to find the user in database
                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    String foundID = dataSnapshot.child("id").getValue(String.class);
                    if (foundID.equalsIgnoreCase(uid)) {
                        //Get user instance from database and set user

                        user = dataSnapshot.getValue(User.class);
                        user.setId(uid);
                        DatabaseReference db = mDataref.child("users").child(user.getId()).child("liked");
                        Log.e("Noofitem1", db.toString());
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    nooflisiting+=1;

                                    Log.e("nofolistingnum",Integer.toString(nooflisiting));
                                    Log.e("Noofitem", db.toString());

                                }
                                TextView nolisting = (TextView) view.findViewById(R.id.listing_numbers);
                                nolisting.setText(Integer.toString(nooflisiting));


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


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


        //When logged out, will send user back to log-in page and finish the activtiy and sign out from current authenticated instance
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Clear events
                Event.eventsList.clear();
                //Create intent
                Intent intent = new Intent(getActivity(), loginpage.class);
                //If sign out have problem, create toast message informing user of problem
                try {
                    //sign out from user


                    auth.signOut();
                    //Inform user activity finished
                    Toast.makeText(getContext(),"Sign-Out sucessful!",Toast.LENGTH_SHORT).show();
                    //Got to login page activity

                    startActivity(intent);
                    //Finish activity
                    getActivity().finish();
                }
                catch(Exception e){
                    //Error message
                    Toast.makeText(getContext(),"Something went wrong. Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Get views
        CardView eventcard = (CardView)view.findViewById(R.id.event_card);
        CardView listingcard = (CardView) view.findViewById(R.id.listing_card);
        TextView nolisting = (TextView) view.findViewById(R.id.listing_numbers);


        TextView noevents = (TextView)view.findViewById(R.id.num_ofevents);
        int noofevents = numberOfevents(user);
        Log.e("nofolistingnum",Integer.toString(nooflisiting));




        //Set number of events

        noevents.setText(Integer.toString(noofevents));

        eventcard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent toCalender = new Intent(getActivity(), WeekViewActivity.class);
                startActivity(toCalender);
            }
        });
        listingcard.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                replaceFragment(new wishListFragment());
            }
        });
        //Set onclick listner for delete btn
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog("This action is ireversible. Would you want to continue?","Deletion Confirmation",deleteaccount.class);

            }
        });

        //
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                //Set profilepic from selected image in gallery
                uprofilepic.setImageURI(result);
                //Set result url to Imageuri variable
                ImageUri = result;


                //Prevent user from uploading duplicate images when image is in process of loading
                if (muploadtask != null && muploadtask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    //Starts method to upload image to database
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
                    // request permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            YourRequestCode);

                }
                else{
                    launcher.launch("image/*");//get image from gallery and display it
                }


            }
        });


        //Allow Edit Account info
        EditProfilebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent change = new Intent(getActivity(),changeaccdetails.class);
                Log.e("Username",user.getName());
                Log.e("Phonenumber",user.getPhonenumber());
                change.putExtra("User",user);
                startActivity(change);
            }
            
        });

        Button stripeDashboardButton = view.findViewById(R.id.stripeDashboardButton);

        // Get current user id
        auth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = auth.getCurrentUser();
        userId = fbUser.getUid();

        StripeUtils.getStripeAccountId(userId, new ConnectStripeCallback() {
            @Override
            public void stripeAccountIdCallback(String stripeAccountId) {
                if (stripeAccountId != null)
                {
                    stripeDashboardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StripeUtils.createDashboardLink(stripeDialog,
                                    User_Profile_Fragment.this.getActivity(),
                                    stripeAccountId);
                        }
                    });
                }
                else
                {
                    stripeDashboardButton.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // On resume, or when user changes back from calender, updated data is displayed
        final StripeDialog stripeDialog = new StripeDialog(this.getActivity());
        int noofevents = numberOfevents(user);
        TextView noevents = (TextView)getActivity().findViewById(R.id.num_ofevents);
        TextView nolisting = (TextView) getActivity().findViewById(R.id.listing_numbers);
        nolisting.setText(Integer.toString(nooflisiting));
        noevents.setText(Integer.toString(noofevents));

        Button stripeDashboardButton = getActivity().findViewById(R.id.stripeDashboardButton);

        StripeUtils.getStripeAccountId(userId, new ConnectStripeCallback() {
            @Override
            public void stripeAccountIdCallback(String stripeAccountId) {
                if (stripeAccountId != null)
                {
                    stripeDashboardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StripeUtils.createDashboardLink(stripeDialog,
                                    User_Profile_Fragment.this.getActivity(),
                                    stripeAccountId);
                        }
                    });
                }
                else
                {
                    stripeDashboardButton.setVisibility(View.GONE);
                }
            }
        });
    }
    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    boolean isPhone(EditText Phone){
        CharSequence phone = Phone.getText().toString();
        return(!TextUtils.isEmpty(phone)&& Patterns.PHONE.matcher(phone).matches());
    }






    private void alertDialog(String msg, String Title, Class target) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setMessage(msg);
        dialog.setTitle(Title);
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent gotodelete = new Intent(getActivity(),target);
                        startActivity(gotodelete);
                    }
                });
        dialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }


    // Get User image from database
    private void retrieveUserAndDisplayImage(View view) {
        String sid = "";
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        //Get profile picture view
        CircleImageView userPFP = view.findViewById(R.id.uprofilepic);
        //Get authenticated user
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
                //get url link from firebase for image
                String spfp = String.valueOf(result.child("userprofilepic").getValue(String.class));

                if(!TextUtils.isEmpty(spfp)) {//if image is present, image is changed to the appropriate image profile picture, else profile image will remain as default
                    new ImageDownloader(userPFP).execute(spfp);

                }
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit(); // To apply changes
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
        if(ImageUri==null){
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        //Get downloable Url
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Make toast message when sucessful
                                Toast.makeText(getContext(), "Successful.", Toast.LENGTH_SHORT).show();
                                //Set profilepic uri to instance of user
                                user.setUserprofilepic(uri.toString());
                                //upload image url to database
                                mDataref.child("users").child(user.getId()).setValue(user);
                            }

                        });
                    }
                }
            }



        });
    }
     //Get the number of events
    private int numberOfevents(User u){



        // Get events list
        Event[] evenet =Event.eventsList.toArray(new Event[0]);//Get list of events
        int numofevent =0;
        //For every event in list

        for (Event event:evenet){
            //Formater to formate date into yyyy-mm-dd
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Format system date to yyyy-mm-dd
            String now = dtf.format(LocalDateTime.now());
            //Get date from list
            String date = event.getDate().toString();
            Log.e("Date:",date.toString());
            Log.e("Current Date", now.toString());
            //If current date equals to event date, increase numof event by one
            if(now.equals(date)){


                numofevent+=1;
            }
        }
        Log.e("Numofevents",Integer.toString(numofevent));
        return numofevent;

    }
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


