package sg.edu.np.mad_p03_group_gg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class listing_adapter extends RecyclerView.Adapter<listing_viewholder> {
    ArrayList<listingObject> data; //replace with information from DB

    public listing_adapter(ArrayList<listingObject> input) {
        data = input;
    }

    @NonNull
    @Override
    public listing_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card;

        card = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_card, parent, false);

        return new listing_viewholder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull listing_viewholder holder, int position) {
        new ImageDownloader(holder.listing_image).execute("https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/listing-images%2Fi-am-not-a-degenerate-this-is-just-test.jpeg?alt=media&token=d3d97f7a-39ec-4014-ad29-cc9f2bf16368");
        new ImageDownloader(holder.seller_image).execute("https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/user-images%2Fdegeneracy.jpeg?alt=media&token=949a52bf-9c6c-4e27-abfc-3145524e81cd");
        listingObject listing = data.get(position);
        holder.listing_title.setText(listing.getTitle());
        holder.seller_username.setText(listing.getSID());
        holder.price.setText("$" + listing.getPrice());
        holder.itemcondition.setText(listing.getiC());
        // yet to implement image replacement for thumbnail & seller pfp
    }

    @Override
    public int getItemCount() {
        return data.size();
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

    //private void retrieveFromFirebase() {
    //    StorageReference storageRef = storage.getReference;
    //}
}


