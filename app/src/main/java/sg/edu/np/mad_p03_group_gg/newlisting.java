package sg.edu.np.mad_p03_group_gg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class newlisting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlisting);

        ImageView selectimage = findViewById(R.id.choose_image);
        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        Button createlisting = findViewById(R.id.create_listing);
        createlisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToDatabaseAndFirebase();
            }
        });
    }

    private void writeToDatabaseAndFirebase() {
        String dblink = "gs://cashoppe-179d4.appspot.com";
        StorageReference db = FirebaseStorage.getInstance(dblink).getReference().child("listing-images");

        long currenttime = new Date().getTime();
        final StorageReference newfilename = db.child("" + currenttime); //add userid for further uniqueness
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
                                createListingObject(imageUrl);
                            }
                        });
                    }
                }
            }
        });
    }

    private void createListingObject(String url) {
        EditText title_input = findViewById(R.id.input_title);
        EditText price_input = findViewById(R.id.input_price);
        EditText desc_input = findViewById(R.id.input_description);
        EditText address_input = findViewById(R.id.input_address);
        EditText deltype_input = findViewById(R.id.input_deliverytype);
        EditText delprice_input = findViewById(R.id.input_deliveryprice);
        EditText deltime_input = findViewById(R.id.input_deliverytime);

        String title = title_input.getText().toString();
        String price = price_input.getText().toString();
        String desc = desc_input.getText().toString();
        String address = address_input.getText().toString();
        String deltype = deltype_input.getText().toString();
        String delprice = delprice_input.getText().toString();
        String deltime = deltime_input.getText().toString();
        //String lID, String t, String turl, String sid, String sppu, String ic, String p, Boolean r, String desc, String l, Boolean d, String dt, int dp, int dtime

        individualListingObject listing = new individualListingObject(null, title, url, "1", url, "New", price, false, desc, address, false, deltype, delprice, deltime);
        writeToFirebase(listing);
    }

    private void writeToFirebase(individualListingObject listing) {
        String dblink = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app";
        DatabaseReference db = FirebaseDatabase.getInstance(dblink).getReference().child("individual-listing");

        //individualListingObject listing = new individualListingObject("1", "FB test title 1", url, "test seller id 1", url, "New", 10, false, "test description", "ngee ann poly", false, "null", 0, 0);
        db.push().setValue(listing);
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