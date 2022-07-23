package sg.edu.np.mad_p03_group_gg;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class listing_viewholder extends RecyclerView.ViewHolder {
    CardView listing_card;
    ImageView listing_image;
    ImageView seller_image;
    TextView listing_title;
    TextView seller_username;
    TextView price;
    TextView itemcondition;
    TextView listing_reserved_indicator;

    public listing_viewholder(View itemView) {
        super(itemView);

        listing_card = itemView.findViewById(R.id.listing_card);
        listing_image = itemView.findViewById(R.id.listing_image);
        seller_image = itemView.findViewById(R.id.seller_picture);
        listing_title = itemView.findViewById(R.id.listing_title);
        seller_username = itemView.findViewById(R.id.seller_username);
        price = itemView.findViewById(R.id.price);
        itemcondition = itemView.findViewById(R.id.itemcondition);
        listing_reserved_indicator = itemView.findViewById(R.id.listing_reserved_indicator);
    }
}
