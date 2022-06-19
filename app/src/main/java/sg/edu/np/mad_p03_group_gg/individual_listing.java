package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import sg.edu.np.mad_p03_group_gg.chat.Chat;

public class individual_listing extends AppCompatActivity {

    private String uID;
    private String sID;
    private String chatKey = "";
    private User seller;
    private User mainUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_listing);

        Bundle listinginfo = getIntent().getExtras();
        String pID = listinginfo.getString("lID");
        uID = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uID = String.valueOf(user.getUid());
        } else {
            // No user is signed in
        }

        initialCheckLiked(pID, uID);
        createObjectFromFB(pID);
        checkLiked(pID, uID);

        // Get direct chat button from xml
        Button directChat = findViewById(R.id.button_chat);

        // Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get seller id
                for(DataSnapshot dataSnapshot : snapshot.child("individual-listing").getChildren()) {
                    String foundPID = dataSnapshot.getKey();
                    if(pID.equalsIgnoreCase(foundPID)){
                        sID = dataSnapshot.child("sid").getValue(String.class);
                        break;
                    }
                }

                // Get chatkey
                for (DataSnapshot dataSnapshotCurrentChat : snapshot.child("chat").getChildren()){
                    // Get id number of each user
                    String getUserOne = dataSnapshotCurrentChat.child("user1").getValue(String.class);
                    String getUserTwo = dataSnapshotCurrentChat.child("user2").getValue(String.class);

                    // If id numbers are the same as main user and selected user's id number
                    if((TextUtils.equals(getUserOne,sID) && TextUtils.equals(getUserTwo,uID))
                            || (TextUtils.equals(getUserOne,uID) && TextUtils.equals(getUserTwo, sID))){
                        chatKey = dataSnapshotCurrentChat.getKey();
                    }
                }

                // Create seller and main user Object
                for (DataSnapshot dataSnapshotUser : snapshot.child("users").getChildren()){
                    // If id matches main user ID Create main user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),uID)){
                        mainUser = new User (dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class),uID);
                    }
                    // If id matches main SELLER ID Create seller user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),sID)){
                        seller = new User (dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class)
                                ,dataSnapshotUser.child("phonenumber").getValue(String.class)
                                ,dataSnapshotUser.child("userprofilepic").getValue(String.class)
                                ,sID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read from db
            }
        });

        // Send main user and seller info to chat
        directChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(individual_listing.this, Chat.class);
                // Send selected user's data to chat activity
                intent.putExtra("name",seller.getName());
                intent.putExtra("profilePic",seller.getUserprofilepic());
                intent.putExtra("chatKey", chatKey);
                intent.putExtra("id", sID);
                // Send main user data to chat activity
                intent.putExtra("mainUser", (Parcelable) mainUser);

                individual_listing.this.startActivity(intent);
            }
        });

    }

    private void createObjectFromFB(String pid) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        individualdb.getReference().child("individual-listing").child(pid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult()));
                    individualListingObject listing = new individualListingObject();
                    DataSnapshot result = task.getResult();

                    String listingid = result.getKey();
                    String title = result.child("title").getValue(String.class);
                    String thumbnailurl = result.child("tURL").getValue(String.class);
                    String sellerid = result.child("sid").getValue(String.class);
                    String sellerprofilepicurl = result.child("sppu").getValue(String.class);
                    String itemcondition = result.child("iC").getValue(String.class);
                    String price = result.child("price").getValue(String.class);
                    Boolean reserved = result.child("reserved").getValue(Boolean.class);
                    String desc = result.child("description").getValue(String.class);
                    String location = result.child("location").getValue(String.class);
                    Boolean delivery = result.child("delivery").getValue(Boolean.class);
                    String deliverytype = result.child("deliveryType").getValue(String.class);
                    String deliveryprice = result.child("deliveryPrice").getValue(String.class);
                    String deliverytime = result.child("deliveryTime").getValue(String.class);

                    listing = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime);

                    ImageView holder;
                    TextView titleholder;
                    TextView priceholder;
                    TextView itemconditionholder;
                    TextView descriptionholder;
                    TextView locationholder;
                    TextView deliveryoptionholder;
                    TextView deliverypriceholder;
                    TextView deliverytimeholder;

                    holder = findViewById(R.id.imageholder);
                    titleholder = findViewById(R.id.individual_title);
                    priceholder = findViewById(R.id.individual_price);
                    itemconditionholder = findViewById(R.id.individual_itemcondition);
                    descriptionholder = findViewById(R.id.individual_description);
                    locationholder = findViewById(R.id.individual_salelocation);
                    deliveryoptionholder = findViewById(R.id.individual_deliveryoption);
                    deliverypriceholder = findViewById(R.id.individual_deliveryprice);
                    deliverytimeholder = findViewById(R.id.individual_deliverytime);

                    new ImageDownloader(holder).execute(listing.gettURL());
                    titleholder.setText(listing.getTitle());
                    priceholder.setText("$" + listing.getPrice());
                    itemconditionholder.setText("Condition: " + listing.getiC());
                    descriptionholder.setText(listing.getDescription());
                    locationholder.setText(listing.getLocation());
                    deliveryoptionholder.setText(listing.getDeliveryType());
                    deliverypriceholder.setText(listing.getDeliveryPrice());
                    deliverytimeholder.setText(listing.getDeliveryTime());
                }
            }
        });
    }

    private void initialCheckLiked(String pID, String uID) {
        ToggleButton like_button = findViewById(R.id.button_like);

        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked");
        liked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(pID)) {
                    like_button.setChecked(true);
                }

                else {
                    like_button.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkLiked(String pID, String uID) {
        ToggleButton like_button = findViewById(R.id.button_like);

        like_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (like_button.isChecked()) {
                    likeFunction(pID, uID);
                }

                else {
                    unlikeFunction(pID, uID);
                }
            }
        });
    }

    private void likeFunction(String pID, String uID) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked").child(pID);
        liked.setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("LIKE", "successful");
            }
        });
    }

    private void unlikeFunction(String pID, String uID) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked");
        liked.child(pID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("UNLIKE", "successful");
            }
        });;
    }

    /*private void createObjectFromFB(String pid) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        DatabaseReference individualListing = individualdb.getReference().child("individual-listing").child(pid);

        individualListingObject listing;

        individualListing.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnap : snapshot.getChildren()) {
                    String listingid = datasnap.getKey();
                    String title = datasnap.child("title").getValue(String.class);
                    String thumbnailurl = datasnap.child("tURL").getValue(String.class);
                    String sellerid = datasnap.child("sid").getValue(String.class);
                    String sellerprofilepicurl = datasnap.child("sppu").getValue(String.class);
                    String itemcondition = datasnap.child("iC").getValue(String.class);
                    String price = datasnap.child("price").getValue(String.class);
                    Boolean reserved = datasnap.child("reserved").getValue(Boolean.class);
                    String desc = datasnap.child("description").getValue(String.class);
                    String location = datasnap.child("location").getValue(String.class);
                    Boolean delivery = datasnap.child("delivery").getValue(Boolean.class);
                    String deliverytype = datasnap.child("deliveryType").getValue(String.class);
                    String deliveryprice = datasnap.child("deliveryPrice").getValue(String.class);
                    String deliverytime = datasnap.child("deliveryTime").getValue(String.class);

                    individualListingObject listing = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime);

                    ImageView holder;
                    TextView titleholder;
                    TextView priceholder;
                    TextView itemconditionholder;
                    TextView descriptionholder;
                    TextView locationholder;
                    TextView deliveryoptionholder;
                    TextView deliverypriceholder;
                    TextView deliverytimeholder;

                    holder = findViewById(R.id.imageholder);
                    titleholder = findViewById(R.id.individual_title);
                    priceholder = findViewById(R.id.individual_price);
                    itemconditionholder = findViewById(R.id.individual_itemcondition);
                    descriptionholder = findViewById(R.id.individual_description);
                    locationholder = findViewById(R.id.individual_salelocation);
                    deliveryoptionholder = findViewById(R.id.individual_deliveryoption);
                    deliverypriceholder = findViewById(R.id.individual_deliveryprice);
                    deliverytimeholder = findViewById(R.id.individual_deliverytime);

                    new ImageDownloader(holder).execute(listing.gettURL());
                    titleholder.setText(listing.getTitle());
                    priceholder.setText("" + listing.getPrice());
                    itemconditionholder.setText(listing.getiC());
                    descriptionholder.setText(listing.getDescription());
                    locationholder.setText(listing.getLocation());
                    deliveryoptionholder.setText(listing.getDeliveryType());
                    deliverypriceholder.setText(listing.getDeliveryPrice());
                    deliverytimeholder.setText(listing.getDeliveryTime());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

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