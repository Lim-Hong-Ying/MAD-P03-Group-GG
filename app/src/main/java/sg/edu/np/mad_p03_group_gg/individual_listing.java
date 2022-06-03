package sg.edu.np.mad_p03_group_gg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class individual_listing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_listing);

        Bundle listinginfo = getIntent().getExtras();
        individualListingObject listing = createObjectFromFB(listinginfo.getInt("lID"));

        TextView title;
        TextView price;
        TextView itemcondition;
        TextView description;
        TextView location;
        TextView deliveryoption;
        TextView deliveryprice;
        TextView deliverytime;

        title = findViewById(R.id.individual_title);
        price = findViewById(R.id.individual_price);
        itemcondition = findViewById(R.id.individual_itemcondition);
        description = findViewById(R.id.individual_description);
        location = findViewById(R.id.individual_salelocation);
        deliveryoption = findViewById(R.id.individual_deliveryoption);
        deliveryprice = findViewById(R.id.individual_deliveryprice);
        deliverytime = findViewById(R.id.individual_deliverytime);

        title.setText(listing.getTitle());
        price.setText(listing.getPrice());
        itemcondition.setText(listing.getiC());
        description.setText(listing.getDescription());
        location.setText(listing.getLocation());
        deliveryoption.setText(listing.getDeliveryType());
        deliveryprice.setText(listing.getDeliveryPrice());
        deliverytime.setText(listing.getDeliveryTime());
    }

    private individualListingObject createObjectFromFB(int pid) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        DatabaseReference individualListing = individualdb.getReference("individual-listing").child(""+pid);
        individualListing.equalTo(pid);

        individualListingObject listing = new individualListingObject();

        return listing;
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