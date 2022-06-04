package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ImageView listingtest = findViewById(R.id.imageView2);

        listingtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listings = new Intent(Homepage.this, listings.class);
                startActivity(listings);
            }
        });
    }
}