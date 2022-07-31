package sg.edu.np.mad_p03_group_gg;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class imageChooserViewholder extends RecyclerView.ViewHolder {

    ImageView imageHolder;

    public imageChooserViewholder(View itemView) {
        super(itemView);

        imageHolder = itemView.findViewById(R.id.imageHolder);
    }
}
