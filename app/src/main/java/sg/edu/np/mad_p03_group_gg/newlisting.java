package sg.edu.np.mad_p03_group_gg;

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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import sg.edu.np.mad_p03_group_gg.tools.stripe.ConnectWithStripeActivity;

public class newlisting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlisting);

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

                    Button createlisting = findViewById(R.id.create_listing);
                    createlisting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finalCheck(view);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
            }
        });
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        handleIntent(getIntent());
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

        // ############# KAI ZHE PAYMENT SECTION ###############

        Switch stripeSwitch = findViewById(R.id.stripeSwitch);

        stripeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                Intent intent = new Intent(newlisting.this, ConnectWithStripeActivity.class);
                startActivity(intent);
            }
        });

        // ############# END KAI ZHE PAYMENT SECTION ###############
    }

    private void finalCheck(View view) {
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
            writeToDatabaseAndFirebase();

            Intent returnhome = new Intent(view.getContext(), successListPage.class);
            finish();
            view.getContext().startActivity(returnhome);
        }

        else {
            Toast.makeText(getApplicationContext(), "Please enter required information.", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToDatabaseAndFirebase() {
        String dblink = "gs://cashoppe-179d4.appspot.com";
        StorageReference db = FirebaseStorage.getInstance(dblink).getReference().child("listing-images");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            final String sID = String.valueOf(user.getUid());

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
                                    createListingObject(imageUrl, sID);
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

    private void createListingObject(String url, String sID) {
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

        if (!meeting_toggle.isChecked()) {
            address = "";
        }

        if (!delivery_toggle.isChecked()) {
            deltype = "";
            delprice = "";
            deltime = "";
        }

        //String lID, String t, String turl, String sid, String sppu, String ic, String p, Boolean r, String desc, String l, Boolean d, String dt, int dp, int dtime

        individualListingObject listing = new individualListingObject(null, title, url, sID, url, condition, price, false, desc, address, false, deltype, delprice, deltime);
        writeToFirebase(listing);
    }

    private String writeToFirebase(individualListingObject listing) {
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");

        //individualListingObject listing = new individualListingObject("1", "FB test title 1", url, "test seller id 1", url, "New", 10, false, "test description", "ngee ann poly", false, "null", 0, 0);
        DatabaseReference pushTask = db.push();
        Task<Void> setValue = pushTask.setValue(listing);
        String pID = String.valueOf(pushTask.getKey());
        return pID;
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

    /**
     * Handle app links. To handle refresh_url from Stripe
     *
     * By: Kai Zhe
     * @param intent
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            /*String recipeId = appLinkData.getLastPathSegment();
            Uri appData = Uri.parse("content://com.recipe_app/recipe/").buildUpon()
                    .appendPath(recipeId).build();
            showRecipe(appData);*/
        }
    }
}