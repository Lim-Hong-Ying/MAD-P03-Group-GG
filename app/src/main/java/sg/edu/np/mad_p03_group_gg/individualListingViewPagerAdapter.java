package sg.edu.np.mad_p03_group_gg;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class individualListingViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> imageURLs = new ArrayList<>();
    LayoutInflater inflater;

    public individualListingViewPagerAdapter(Context context, ArrayList<String> imageURLs) {
        this.context = context;
        this.imageURLs = imageURLs;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ConstraintLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        Log.e("URLS", String.valueOf(imageURLs));
        View itemView = inflater.inflate(R.layout.view_pager_holder, container, false);
        ImageView holder = itemView.findViewById(R.id.vp_imageholder);
        Picasso.get().load(imageURLs.get(position)).into(holder); //External library to download images
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
