package sg.edu.np.mad_p03_group_gg;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

        Picasso.get().load(image).into(holder.imageHolder); //External library to download images
        /*holder.imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, position);
            }
        });*/
        //holder.imageHolder.setImageURI(image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.image_chooser, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure you want to delete this image?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;

                    case R.id.replace:

                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
}
