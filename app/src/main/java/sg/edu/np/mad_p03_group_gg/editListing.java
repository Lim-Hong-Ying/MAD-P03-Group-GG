package sg.edu.np.mad_p03_group_gg;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class editListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlisting);

        TextView header = findViewById(R.id.textView9);
        header.setText("Edit Listing");

        Button createListing = findViewById(R.id.create_listing);
        createListing.setText("Update listing!");

        Bundle postID = getIntent().getExtras();
        String pID = postID.getString("pID");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    activeChecker();

                    ImageButton back_button = findViewById(R.id.back_button);
                    back_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                    createObjectFromFB(pID);
                }

                else {
                    Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createObjectFromFB(String pID) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/"; //Points to Firebase Database
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db); //Retrieves information
        individualdb.getReference().child("individual-listing").child(pID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                }
                else { //Builds individualListingObject from data retrieved
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
                    EditText titleholder;
                    EditText priceholder;
                    RadioGroup itemconditionholder;
                    EditText descriptionholder;
                    Switch meettoggle;
                    EditText locationholder;
                    Switch deliverytoggle;
                    EditText deliveryoptionholder;
                    EditText deliverypriceholder;
                    EditText deliverytimeholder;

                    holder = findViewById(R.id.imageholder);
                    titleholder = findViewById(R.id.input_title);
                    priceholder = findViewById(R.id.input_price);
                    itemconditionholder = findViewById(R.id.input_condition);
                    descriptionholder = findViewById(R.id.input_description);
                    meettoggle = findViewById(R.id.meet_toggle);
                    locationholder = findViewById(R.id.input_address);
                    deliverytoggle = findViewById(R.id.del_toggle);
                    deliveryoptionholder = findViewById(R.id.input_deliverytype);
                    deliverypriceholder = findViewById(R.id.input_deliveryprice);
                    deliverytimeholder = findViewById(R.id.input_deliverytime);

                    //Picasso.get().load(listing.gettURL()).into(holder); //External library to download images
                    //new ImageDownloader(holder).execute(listing.gettURL());

                    titleholder.setText(listing.getTitle());
                    priceholder.setText(listing.getPrice());
                    descriptionholder.setText(listing.getDescription());
                    locationholder.setText(listing.getLocation());
                    deliveryoptionholder.setText(listing.getDeliveryType());
                    deliverypriceholder.setText(listing.getDeliveryPrice());
                    deliverytimeholder.setText(listing.getDeliveryTime());

                    switch (itemcondition) {
                        case "New":
                            itemconditionholder.check(R.id.input_condition_new);
                            break;

                        case "Used":
                            itemconditionholder.check(R.id.input_condition_used);
                            break;
                    }

                    if (location != "") {
                        meettoggle.setChecked(true);
                    }

                    if (delivery != false) {
                        deliverytoggle.setChecked(true);
                    }

                    Button updateListing = findViewById(R.id.create_listing);
                    updateListing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finalCheck(view, pID, sellerid);
                        }
                    });
                }
            }
        });
    }

    private void activeChecker() {
        EditText title_input = findViewById(R.id.input_title);
        EditText price_input = findViewById(R.id.input_price);
        RadioGroup condition_input = findViewById(R.id.input_condition);
        RadioButton condition_input_new = findViewById(R.id.input_condition_new);
        RadioButton condition_input_used = findViewById(R.id.input_condition_used);
        EditText desc_input = findViewById(R.id.input_description);
        EditText address_input = findViewById(R.id.input_address);
        EditText deltype_input = findViewById(R.id.input_deliverytype);
        EditText delprice_input = findViewById(R.id.input_deliveryprice);
        EditText deltime_input = findViewById(R.id.input_deliverytime);
        Switch meeting_toggle = findViewById(R.id.meet_toggle);
        Switch delivery_toggle = findViewById(R.id.del_toggle);

        address_input.setVisibility(View.GONE);
        deltype_input.setVisibility(View.GONE);
        delprice_input.setVisibility(View.GONE);
        deltime_input.setVisibility(View.GONE);

        ImageView selectimage = findViewById(R.id.choose_image);
        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        title_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(title_input.getText().toString())) {
                    title_input.setError("A title is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        price_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(price_input.getText().toString())) {
                    price_input.setError("A price is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        desc_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(desc_input.getText().toString())) {
                    desc_input.setError("A description is required");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        meeting_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    address_input.setVisibility(View.VISIBLE);
                    address_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            if (TextUtils.isEmpty(address_input.getText().toString())) {
                                address_input.setError("An address is required");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

                else {
                    address_input.setVisibility(View.GONE);
                }
            }
        });

        delivery_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    deltype_input.setVisibility(View.VISIBLE);
                    delprice_input.setVisibility(View.VISIBLE);
                    deltime_input.setVisibility(View.VISIBLE);

                    deltype_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (TextUtils.isEmpty(deltype_input.getText().toString())) {
                                deltype_input.setError("A delivery type is required");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    delprice_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (TextUtils.isEmpty(delprice_input.getText().toString())) {
                                delprice_input.setError("A price is required");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    deltime_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (TextUtils.isEmpty(deltime_input.getText().toString())) {
                                deltime_input.setError("A delivery estimate is required");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

                else {
                    deltype_input.setVisibility(View.GONE);
                    delprice_input.setVisibility(View.GONE);
                    deltime_input.setVisibility(View.GONE);
                }
            }
        });
    }

    private void finalCheck(View view, String pID, String sID) {
        EditText title_input = findViewById(R.id.input_title);
        EditText price_input = findViewById(R.id.input_price);
        RadioGroup condition_input = findViewById(R.id.input_condition);
        EditText desc_input = findViewById(R.id.input_description);
        EditText address_input = findViewById(R.id.input_address);
        EditText deltype_input = findViewById(R.id.input_deliverytype);
        EditText delprice_input = findViewById(R.id.input_deliveryprice);
        EditText deltime_input = findViewById(R.id.input_deliverytime);
        Switch meeting_toggle = findViewById(R.id.meet_toggle);
        Switch delivery_toggle = findViewById(R.id.del_toggle);

        Boolean image_selected = true;
        Boolean title_filled = false;
        Boolean price_filled = false;
        Boolean desc_filled = false;
        Boolean itemcondition_selected = false;
        Boolean meetup_filled = false;
        Boolean deliverytype_filled = false;
        Boolean deliveryprice_filled = false;
        Boolean deliverytime_filled = false;

        if (!meeting_toggle.isChecked()) {
            meetup_filled = true;
        }

        if (!delivery_toggle.isChecked()) {
            deliverytype_filled = true;
            deliveryprice_filled = true;
            deliverytime_filled = true;
        }

        if (TextUtils.isEmpty(title_input.getText().toString()) == false) {
            title_filled = true;
        }

        if (TextUtils.isEmpty(price_input.getText().toString()) == false) {
            price_filled = true;
        }

        if (condition_input.getCheckedRadioButtonId() != -1) {
            itemcondition_selected = true;
        }

        if (TextUtils.isEmpty(desc_input.getText().toString()) == false) {
            desc_filled = true;
        }

        if (TextUtils.isEmpty(address_input.getText().toString()) == false) {
            meetup_filled = true;
        }

        if (TextUtils.isEmpty(deltype_input.getText().toString()) == false) {
            deliverytype_filled = true;
        }

        if (TextUtils.isEmpty(delprice_input.getText().toString()) == false) {
            deliveryprice_filled = true;
        }

        if (TextUtils.isEmpty(deltime_input.getText().toString()) == false) {
            deliverytime_filled = true;
        }

        if (image_selected == true && title_filled == true && price_filled == true && itemcondition_selected == true && desc_filled == true && meetup_filled == true && deliverytype_filled == true && deliveryprice_filled == true && deliverytime_filled == true) {
            writeToDatabaseAndFirebase(pID, sID);
        }

        else {
            Toast.makeText(getApplicationContext(), "Please enter required information.", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToDatabaseAndFirebase(String pID, String sID) {
        String dblink = "gs://cashoppe-179d4.appspot.com";
        StorageReference db = FirebaseStorage.getInstance(dblink).getReference().child("listing-images");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

            long currenttime = new Date().getTime();
            final StorageReference newfilename = db.child(sID + currenttime); //add userid for further uniqueness
            ImageView selectimage = findViewById(R.id.choose_image);

            selectimage.setDrawingCacheEnabled(true);
            selectimage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) selectimage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = newfilename.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    createListingObject(imageUrl, sID, pID);
                                }
                            });
                        }
                    }
                }
            });
        }

        else {
            Toast.makeText(getApplicationContext(), "Not logged in. Please relogin.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createListingObject(String url, String sID, String pID) {
        EditText title_input = findViewById(R.id.input_title);
        EditText price_input = findViewById(R.id.input_price);
        RadioGroup condition_input = findViewById(R.id.input_condition);
        RadioButton condition_input_new = findViewById(R.id.input_condition_new);
        RadioButton condition_input_used = findViewById(R.id.input_condition_used);
        EditText desc_input = findViewById(R.id.input_description);
        EditText address_input = findViewById(R.id.input_address);
        EditText deltype_input = findViewById(R.id.input_deliverytype);
        EditText delprice_input = findViewById(R.id.input_deliveryprice);
        EditText deltime_input = findViewById(R.id.input_deliverytime);
        Switch meeting_toggle = findViewById(R.id.meet_toggle);
        Switch delivery_toggle = findViewById(R.id.del_toggle);

        String title = title_input.getText().toString();
        String price = price_input.getText().toString();
        String condition;
        if (condition_input_new.isChecked()) {
            condition = "New";
        }

        else if (condition_input_used.isChecked()){
            condition = "Used";
        }

        else {
            condition = null;
        }

        String desc = desc_input.getText().toString();
        String address = address_input.getText().toString();
        String deltype = deltype_input.getText().toString();
        String delprice = delprice_input.getText().toString();
        String deltime = deltime_input.getText().toString();
        Boolean delivery = true;

        if (!meeting_toggle.isChecked()) {
            address = "";
        }

        if (!delivery_toggle.isChecked()) {
            delivery = false;
            deltype = "";
            delprice = "";
            deltime = "";
        }

        //String lID, String t, String turl, String sid, String sppu, String ic, String p, Boolean r, String desc, String l, Boolean d, String dt, int dp, int dtime

        individualListingObject listing = new individualListingObject(null, title, url, sID, url, condition, price, false, desc, address, delivery, deltype, delprice, deltime);
        writeToFirebase(listing, pID);
    }

    private void writeToFirebase(individualListingObject listing, String pID) {
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");

        //individualListingObject listing = new individualListingObject("1", "FB test title 1", url, "test seller id 1", url, "New", 10, false, "test description", "ngee ann poly", false, "null", 0, 0);
        //DatabaseReference pushTask = db.push();
        //Task<Void> setValue = pushTask.setValue(listing);
        //String pID = String.valueOf(pushTask.getKey());
        db.child(pID).setValue(listing);

        Bundle listingInfo = new Bundle();
        listingInfo.putString("pID", pID);
        listingInfo.putString("type", "edit");

        Intent successEdit = new Intent(editListing.this, successListPage.class);
        successEdit.putExtras(listingInfo);
        finish();
        editListing.this.startActivity(successEdit);
    }

    private void chooseImage() {
        Intent chooser = new Intent();
        chooser.setType("image/*");
        chooser.setAction(Intent.ACTION_GET_CONTENT);
        launchPicker.launch(chooser);
    }

    ActivityResultLauncher<Intent> launchPicker
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap = null;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView selectimage = findViewById(R.id.choose_image);
                selectimage.setImageBitmap(selectedImageBitmap);
            }
        }
    });
}