package sg.edu.np.mad_p03_group_gg;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class imageChooserAdapter extends RecyclerView.Adapter<imageChooserViewholder> {
    ArrayList<Uri> data = new ArrayList<>();

    public imageChooserAdapter(ArrayList<Uri> input) {
        data = input;
    }

    @NonNull
    @Override
    public imageChooserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageHolder;
        imageHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chooser, parent, false);

        return new imageChooserViewholder(imageHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull imageChooserViewholder holder, int position) {
        Uri image = data.get(position);

        holder.imageHolder.setImageURI(image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
