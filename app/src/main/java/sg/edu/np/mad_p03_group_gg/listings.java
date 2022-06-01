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

        test = testlistings(test);

        RecyclerView listingRecycler = findViewById(R.id.listing_recycler);
        listing_adapter adapter = new listing_adapter(test);

        LinearLayoutManager listingLayoutMgr = new LinearLayoutManager(this);
        listingRecycler.setLayoutManager(listingLayoutMgr);
        listingRecycler.setItemAnimator(new DefaultItemAnimator());
        listingRecycler.setAdapter(adapter);

    }

    private ArrayList<listingObject> testlistings(ArrayList<listingObject> test) {
        String testthumbnail = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/listing-images%2Fi-am-not-a-degenerate-this-is-just-test.jpeg?alt=media&token=d3d97f7a-39ec-4014-ad29-cc9f2bf16368";
        String testpfp = "https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/user-images%2Fdegeneracy.jpeg?alt=media&token=949a52bf-9c6c-4e27-abfc-3145524e81cd";
        listingObject test1 = new listingObject(1, "test title 1", testthumbnail, "test seller id 1", testpfp, "New", 10, false);
        listingObject test2 = new listingObject(2, "test title 2", testthumbnail, "test seller id 2", testpfp, "Used", 100, true);
        listingObject test3 = new listingObject(3, "test title 3", testthumbnail, "test seller id 3", testpfp, "New", 200, false);
        listingObject test4 = new listingObject(4, "test title 4", testthumbnail, "test seller id 4", testpfp, "Used", 300, false);
        listingObject test5 = new listingObject(5, "test title 5", testthumbnail, "test seller id 5", testpfp, "New", 500, true);

        test.add(test1);
        test.add(test2);
        test.add(test3);
        test.add(test4);
        test.add(test5);

        return test;
    }
}