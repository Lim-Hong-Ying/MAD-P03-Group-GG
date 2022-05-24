package sg.edu.np.mad_p03_group_gg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class listing_viewholder extends RecyclerView.ViewHolder {
    ImageView listing_image;
    ImageView seller_image;
    TextView listing_title;
    TextView seller_username;

    public listing_viewholder(View itemView) {
        super(itemView);

        listing_image = itemView.findViewById(R.id.listing_image);
        seller_image = itemView.findViewById(R.id.seller_picture);
        listing_title = itemView.findViewById(R.id.listing_title);
        seller_username = itemView.findViewById(R.id.seller_username);
    }
}
