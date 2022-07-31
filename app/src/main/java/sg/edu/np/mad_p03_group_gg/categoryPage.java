package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class categoryPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        buttonListeners();
    }

    private void buttonListeners() {
        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CardView allListings = findViewById(R.id.all_listings_card);
        CardView electronics = findViewById(R.id.electronics_card);
        CardView education = findViewById(R.id.education_card);
        CardView automobiles = findViewById(R.id.automobiles_card);
        CardView furniture = findViewById(R.id.furniture_card);
        CardView fashion = findViewById(R.id.fashion_card);
        CardView services = findViewById(R.id.services_card);
        CardView jobs = findViewById(R.id.jobs_card);
        CardView property = findViewById(R.id.property_card);
        CardView sports = findViewById(R.id.sports_card);
        CardView pets = findViewById(R.id.pets_card);
        CardView freeItems = findViewById(R.id.free_items_card);

        allListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "electronics");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "education");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        automobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "automobiles");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "furniture");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "fashion");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "services");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "jobs");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "property");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "sports");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        pets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "pets");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });

        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle categoryInfo = new Bundle();
                categoryInfo.putString("category", "jobs");
                Intent listingsPage = new Intent(categoryPage.this, listingsPage.class);
                listingsPage.putExtras(categoryInfo);
                view.getContext().startActivity(listingsPage);
            }
        });
    }
}