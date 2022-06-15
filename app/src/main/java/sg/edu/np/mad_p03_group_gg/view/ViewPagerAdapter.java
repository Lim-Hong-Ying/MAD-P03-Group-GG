package sg.edu.np.mad_p03_group_gg.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Advanceable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.models.AdBannerImage;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.RecyclerViewHolder> {
    ArrayList<AdBannerImage> adBannerImages;

    public ViewPagerAdapter(ArrayList<AdBannerImage> adBannerImages) {
        this.adBannerImages = adBannerImages;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pager_item, parent, false);;

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        AdBannerImage adBannerImage = adBannerImages.get(position);
        Bitmap image = BitmapFactory.decodeFile(adBannerImage.getImage());
        holder.adBannerImageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return adBannerImages.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView adBannerImageView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            adBannerImageView = itemView.findViewById(R.id.imageView);
        }
    }
}