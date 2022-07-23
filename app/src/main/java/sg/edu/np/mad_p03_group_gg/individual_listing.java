package sg.edu.np.mad_p03_group_gg;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sg.edu.np.mad_p03_group_gg.chat.Chat;
import sg.edu.np.mad_p03_group_gg.view.ViewPagerAdapter;
import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;

public class individual_listing extends AppCompatActivity {

    private String uID;
    private String sID;
    private String chatKey = "";
    private User seller;
    private User mainUser;

    private String pID;
    private individualListingObject listing = new individualListingObject();

    String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/"; //Points to Firebase Database
    FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db); //Retrieves information

    String storagelink = "gs://cashoppe-179d4.appspot.com";
    StorageReference storage = FirebaseStorage.getInstance(storagelink).getReference().child("listing-images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_listing);

        Bundle listinginfo = getIntent().getExtras();
        pID = listinginfo.getString("lID");
        storage = storage.child(pID);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Gets userID for logged in user
        if (user != null) {
            // User is signed in
            uID = String.valueOf(user.getUid());
        } else {
            // No user is signed in
        }

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView contextMenu = findViewById(R.id.context_menu);
        contextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        DatabaseReference connectedRef = individualdb.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    initialCheckLiked();
                    createObjectFromFB(pID, uID);
                    checkLiked();
                } else {
                    Toast.makeText(individual_listing.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(individual_listing.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
            }
        });

        //############## WILLIAM CHAT SECTION ##################

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

                // Add seller to friend list
                databaseReference.child("selectedChatUsers").child(mainUser.getId())
                        .child(sID).setValue("");

                // Start chat activity
                individual_listing.this.startActivity(intent);
            }
        });
        // ############# END WILLIAM SECTION ###############
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.individual_listing_seller, popup.getMenu());
        if (listing.getReserved() == false) {
            MenuItem unreserve = popup.getMenu().findItem(R.id.unreserve);
            unreserve.setVisible(false);
        }

        else {
            MenuItem reserve = popup.getMenu().findItem(R.id.reserve);
            reserve.setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(individual_listing.this);

                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure you want to delete this listing?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteListing();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;

                    case R.id.edit:
                        Bundle listingID = new Bundle();
                        listingID.putString("pID", pID);

                        Intent editListing = new Intent(individual_listing.this, editListing.class);
                        editListing.putExtras(listingID);
                        startActivity(editListing);
                        return true;

                    case R.id.reserve:
                        reserveListing();
                        return true;

                    case R.id.unreserve:
                        unreserveListing();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void createObjectFromFB(String pID, String uID) {
        individualdb.getReference().child("individual-listing").child(pID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(individual_listing.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                }
                else { //Builds individualListingObject from data retrieved
                    Log.d("firebase", String.valueOf(task.getResult()));
                    //individualListingObject listing = new individualListingObject();
                    DataSnapshot result = task.getResult();

                    String listingid = result.getKey();
                    String title = result.child("title").getValue(String.class);
                    //String thumbnailurl = result.child("tURL").getValue(String.class);
                    long thumbnailurlsize = result.child("tURLs").getChildrenCount();
                    ArrayList<String> tURLs = new ArrayList<>();
                    for (int i = 0; i < thumbnailurlsize; i++) {
                        tURLs.add(result.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                    }
                    String sellerid = result.child("sid").getValue(String.class);
                    //String sellerprofilepicurl = result.child("sppu").getValue(String.class);
                    String itemcondition = result.child("iC").getValue(String.class);
                    String price = result.child("price").getValue(String.class);
                    Boolean reserved = result.child("reserved").getValue(Boolean.class);
                    String desc = result.child("description").getValue(String.class);
                    String location = result.child("location").getValue(String.class);
                    Boolean delivery = result.child("delivery").getValue(Boolean.class);
                    String deliverytype = result.child("deliveryType").getValue(String.class);
                    String deliveryprice = result.child("deliveryPrice").getValue(String.class);
                    String deliverytime = result.child("deliveryTime").getValue(String.class);
                    String TimeStamp = result.child("timeStamp").getValue(String.class);

                    listing = new individualListingObject(listingid, title, tURLs, sellerid, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime, TimeStamp);

                    TextView titleholder;
                    TextView priceholder;
                    TextView itemconditionholder;
                    TextView descriptionholder;
                    TextView locationholder;
                    TextView deliveryoptionholder;
                    TextView deliverypriceholder;
                    TextView deliverytimeholder;

                    titleholder = findViewById(R.id.individual_title);
                    priceholder = findViewById(R.id.individual_price);
                    itemconditionholder = findViewById(R.id.individual_itemcondition);
                    descriptionholder = findViewById(R.id.individual_description);
                    locationholder = findViewById(R.id.individual_salelocation);
                    deliveryoptionholder = findViewById(R.id.individual_deliveryoption);
                    deliverypriceholder = findViewById(R.id.individual_deliveryprice);
                    deliverytimeholder = findViewById(R.id.individual_deliverytime);

                    ViewPager viewPager = findViewById(R.id.viewPagerMain);
                    individualListingViewPagerAdapter viewPagerAdapter = new individualListingViewPagerAdapter(individual_listing.this, listing.gettURLs());
                    viewPager.setAdapter(viewPagerAdapter);

                    //Picasso.get().load(listing.gettURLs().get(0)).into(holder); //External library to download images
                    //new ImageDownloader(holder).execute(listing.gettURL());
                    titleholder.setText(listing.getTitle());
                    priceholder.setText("$" + listing.getPrice());
                    itemconditionholder.setText("Condition: " + listing.getiC());
                    descriptionholder.setText(listing.getDescription());

                    if (!listing.getLocation().isEmpty()) {
                        locationholder.setText("Address: " + listing.getLocation());
                    }

                    else {
                        TextView addressheader = findViewById(R.id.individual_saleoptionheader);
                        addressheader.setVisibility(View.GONE);
                        locationholder.setVisibility(View.GONE);
                    }

                    if (!listing.getDeliveryType().isEmpty()) {
                        deliveryoptionholder.setText("Delivery type: " + listing.getDeliveryType());
                        deliverypriceholder.setText("Delivery price: $" + listing.getDeliveryPrice());
                        deliverytimeholder.setText("Estimated delivery time: " + listing.getDeliveryTime() + " days");
                    }

                    else {
                        TextView deliveryheader = findViewById(R.id.individual_deliveryheader);
                        deliveryheader.setVisibility(View.GONE);
                        deliveryoptionholder.setVisibility(View.GONE);
                        deliverypriceholder.setVisibility(View.GONE);
                        deliverytimeholder.setVisibility(View.GONE);
                    }

                    if (sellerid.equals(uID)) {
                        ToggleButton likebutton = findViewById(R.id.button_like);
                        Button chatbutton = findViewById(R.id.button_chat);

                        likebutton.setVisibility(View.GONE);
                        chatbutton.setVisibility(View.GONE);
                    }

                    else {
                        ImageView contextMenu = findViewById(R.id.context_menu);
                        contextMenu.setVisibility(View.GONE);
                        if (reserved) {
                            Button chatbutton = findViewById(R.id.button_chat);
                            chatbutton.setText("Item reserved");
                            chatbutton.setEnabled(false);
                        }
                    }

                    //Retrieves seller's username and downloads image if available
                    individualdb.getReference().child("users").child(sellerid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();
                            String sellername = String.valueOf(result.child("name").getValue(String.class));
                            String SPPU = String.valueOf(result.child("userprofilepic").getValue(String.class));

                            ImageView sellerpfp = findViewById(R.id.seller_pfp);
                            TextView name_holder = findViewById(R.id.seller_name);

                            name_holder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle seller_id = new Bundle();
                                    seller_id.putString("uid", sellerid);

                                    Intent profilepage = new Intent(individual_listing.this, userProfile.class);
                                    profilepage.putExtras(seller_id);
                                    individual_listing.this.startActivity(profilepage);
                                }
                            });

                            name_holder.setText(sellername);
                            if (!SPPU.isEmpty()) {
                                Picasso.get().load(SPPU).into(sellerpfp); //External library to download images
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(individual_listing.this, "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initialCheckLiked() { //Checks for like status to set like button on initial start
        ToggleButton like_button = findViewById(R.id.button_like);

        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked"); //Points to correct child directory
        liked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("snapshot", String.valueOf(snapshot));
                if (snapshot.exists()) {
                    if (snapshot.hasChild(pID)) {
                        like_button.setChecked(true);
                    }

                    else {
                        like_button.setChecked(false);
                    }
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

    private void checkLiked() { //Changes like status and updates database
        ToggleButton like_button = findViewById(R.id.button_like);

        like_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (like_button.isChecked()) {
                    likeFunction();
                }

                else {
                    unlikeFunction();
                }
            }
        });
    }

    private void likeFunction() { //Updates database when liking objects
        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked").child(pID);
        liked.setValue("");
    }

    private void unlikeFunction() { //Updates database when unliking objects
        DatabaseReference liked = individualdb.getReference().child("users").child(uID).child("liked");
        liked.child(pID).removeValue();
    }

    private void reserveListing() {
        individualdb.getReference().child("individual-listing").child(pID).child("reserved").setValue(true);
        listing.setReserved(true);
        Toast.makeText(individual_listing.this, "Marked listing as reserved.", Toast.LENGTH_SHORT).show();
    }

    private void unreserveListing() {
        individualdb.getReference().child("individual-listing").child(pID).child("reserved").setValue(false);
        listing.setReserved(false);
        Toast.makeText(individual_listing.this, "Marked listing as available.", Toast.LENGTH_SHORT).show();
    }

    private void deleteListing() {
        individualdb.getReference().child("individual-listing").child(pID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                storage.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            item.delete();
                        }

                        individualdb.getReference().child("users").child(uID).child("listings").child(pID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                Toast.makeText(individual_listing.this, "Deleted listing!", Toast.LENGTH_SHORT).show();
                                Intent returnHome = new Intent(individual_listing.this, MainActivity.class);
                                returnHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(returnHome);
                            }
                        });
                    }
                });
            }
        });
    }
}