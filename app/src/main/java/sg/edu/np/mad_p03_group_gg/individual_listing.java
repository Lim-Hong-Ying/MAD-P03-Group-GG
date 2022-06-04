package sg.edu.np.mad_p03_group_gg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class individual_listing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_listing);

        Bundle listinginfo = getIntent().getExtras();

        String testthumbnail = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/listing-images%2Fi-am-not-a-degenerate-this-is-just-test.jpeg?alt=media&token=d3d97f7a-39ec-4014-ad29-cc9f2bf16368";
        String testpfp = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/user-images%2Fdegeneracy.jpeg?alt=media&token=949a52bf-9c6c-4e27-abfc-3145524e81cd";
        //individualListingObject listing = new individualListingObject("1", "FB test title 1", testthumbnail, "test seller id 1", testpfp, "New", "10", false, "test description", "ngee ann poly", false, "null", "0", "0");

        individualListingObject listing = new individualListingObject();

        ImageView holder;
        TextView title;
        TextView price;
        TextView itemcondition;
        TextView description;
        TextView location;
        TextView deliveryoption;
        TextView deliveryprice;
        TextView deliverytime;

        holder = findViewById(R.id.imageholder);
        title = findViewById(R.id.individual_title);
        price = findViewById(R.id.individual_price);
        itemcondition = findViewById(R.id.individual_itemcondition);
        description = findViewById(R.id.individual_description);
        location = findViewById(R.id.individual_salelocation);
        deliveryoption = findViewById(R.id.individual_deliveryoption);
        deliveryprice = findViewById(R.id.individual_deliveryprice);
        deliverytime = findViewById(R.id.individual_deliverytime);

        new ImageDownloader(holder).execute(listing.gettURL());
        title.setText(listing.getTitle());
        price.setText("" + listing.getPrice());
        itemcondition.setText(listing.getiC());
        description.setText(listing.getDescription());
        location.setText(listing.getLocation());
        deliveryoption.setText(listing.getDeliveryType());
        deliveryprice.setText(listing.getDeliveryPrice());
        deliverytime.setText(listing.getDeliveryTime());

        createObjectFromFB(listinginfo.getString("lID")); //thing doesnt work :(
    }

    private void createObjectFromFB(String pid) {
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
    }

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