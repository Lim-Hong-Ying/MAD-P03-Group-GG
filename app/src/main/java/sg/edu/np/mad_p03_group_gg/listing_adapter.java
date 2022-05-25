package sg.edu.np.mad_p03_group_gg;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class listing_adapter extends RecyclerView.Adapter<listing_viewholder> {
    ArrayList<String> data; //replace with information from DB

    @NonNull
    @Override
    public listing_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card;

        card = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_card, parent, false);

        return new listing_viewholder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull listing_viewholder holder, int position) {
        String listing = data.get(position);
        //holder.listing_title.setText();
        //holder.seller_username.setText();


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            return null;
        }
    }
}


