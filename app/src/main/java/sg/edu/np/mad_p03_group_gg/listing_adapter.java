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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

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
    public listing_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Creates viewholders, chooses between 2 view modes
        View card;

        SharedPreferences sharedPreferences = parent.getContext().getSharedPreferences("Cashopee", MODE_PRIVATE);

        String mode = sharedPreferences.getString("view", "");

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
        Log.e("listing value", String.valueOf(listing));
        if (listing.gettURLs().size() != 0) {
            Picasso.get().load(listing.gettURLs().get(0)).into(holder.listing_image); //External library to download images
        }

        holder.listing_title.setText(listing.getTitle());
        holder.price.setText("$" + listing.getPrice());
        holder.itemcondition.setText(listing.getiC());

        if (listing.getReserved() == false) {
            holder.listing_reserved_indicator.setVisibility(View.GONE);
        }

        //Downloads information from Firebase Database
        String sid = "";
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db);
        individualdb.getReference().child("users").child(listing.getSID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();
                String sid = String.valueOf(result.child("name").getValue(String.class));
                String SPPU = String.valueOf(result.child("userprofilepic").getValue(String.class));

                holder.seller_username.setText(sid);
                if (!SPPU.isEmpty()) {
                    Picasso.get().load(SPPU).into(holder.seller_image); //External library to download images
                }
            }
        });

        holder.listing_card.setOnClickListener(new View.OnClickListener() { //Sets click target to enter individual listing object
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
}


