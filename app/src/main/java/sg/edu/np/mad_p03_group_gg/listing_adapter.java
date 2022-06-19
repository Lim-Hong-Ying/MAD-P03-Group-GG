package sg.edu.np.mad_p03_group_gg;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class listing_adapter extends RecyclerView.Adapter<listing_viewholder> {
    ArrayList<listingObject> data = new ArrayList<>(); //replace with information from DB

    public listing_adapter(ArrayList<listingObject> input) {
        data = input;
    }

    @NonNull
    @Override
    public listing_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card;

        SharedPreferences sharedPreferences = parent.getContext().getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");
        Log.e("mode", mode);

        if (mode == "card") {
            card = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_card, parent, false);
        }

        else {
            card = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_grid, parent, false);
        }

        return new listing_viewholder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull listing_viewholder holder, int position) {
        listingObject listing = data.get(position);
        new ImageDownloader(holder.listing_image).execute(listing.gettURL());

        holder.listing_title.setText(listing.getTitle());
        holder.price.setText("$" + listing.getPrice());
        holder.itemcondition.setText(listing.getiC());

        String sid = "";
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        individualdb.getReference().child("users").child(listing.getSID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();
                Log.e("ss", String.valueOf(result));
                String sid = String.valueOf(result.child("name").getValue(String.class));
                String SPPU = String.valueOf(result.child("userprofilepic").getValue(String.class));
                Log.e("sid", sid);

                holder.seller_username.setText(sid);
                new ImageDownloader(holder.seller_image).execute(SPPU);
            }
        });

        holder.listing_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle listinginfo = new Bundle();
                listinginfo.putString("lID", listing.getlID());
                Intent individuallisting = new Intent(view.getContext(), individual_listing.class);
                individuallisting.putExtras(listinginfo);
                view.getContext().startActivity(individuallisting);
            }
        });
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
}


