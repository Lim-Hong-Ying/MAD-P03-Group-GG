package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class listings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        ArrayList<listingObject> test = new ArrayList<>();
        listingObject test1 = new listingObject(1, "test title 1", "test thumbnail url 1", "test seller id 1", "test seller pp url 1", "New", 10, false);
        listingObject test2 = new listingObject(2, "test title 2", "test thumbnail url 2", "test seller id 2", "test seller pp url 2", "Used", 100, true);
        listingObject test3 = new listingObject(3, "test title 3", "test thumbnail url 3", "test seller id 3", "test seller pp url 3", "New", 200, false);
        listingObject test4 = new listingObject(4, "test title 4", "test thumbnail url 4", "test seller id 4", "test seller pp url 4", "Used", 300, false);
        listingObject test5 = new listingObject(5, "test title 5", "test thumbnail url 5", "test seller id 5", "test seller pp url 5", "New", 500, true);

        test.add(test1);
        test.add(test2);
        test.add(test3);
        test.add(test4);
        test.add(test5);

        RecyclerView listingRecycler = findViewById(R.id.listing_recycler);
        listing_adapter adapter = new listing_adapter(test);

        LinearLayoutManager listingLayoutMgr = new LinearLayoutManager(this);
        listingRecycler.setLayoutManager(listingLayoutMgr);
        listingRecycler.setItemAnimator(new DefaultItemAnimator());
        listingRecycler.setAdapter(adapter);

    }
}