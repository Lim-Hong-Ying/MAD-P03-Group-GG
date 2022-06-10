package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad_p03_group_gg.models.AdBannerImage;
import sg.edu.np.mad_p03_group_gg.view.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Refer to https://www.youtube.com/watch?v=O8LA26sAt7Y
        ArrayList<AdBannerImage> adBannerImages = new ArrayList<>();
        adBannerImages.add(new AdBannerImage(R.drawable.shopee1));
        adBannerImages.add(new AdBannerImage(R.drawable.shopee2));
        ViewPager2 viewPager2 = findViewById(R.id.AdBannerView);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(adBannerImages);

        viewPager2.setAdapter(viewPagerAdapter);


    }
}