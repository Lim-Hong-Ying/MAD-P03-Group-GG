package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import sg.edu.np.mad_p03_group_gg.view.LikedRecyclerAdapter;

public class LikedPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likedpage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // To disable appname title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView closeButton = findViewById(R.id.likedCloseButton);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);

        RecyclerView likedRecyclerView = findViewById(R.id.likedRecyclerView);
        LikedRecyclerAdapter likedRecyclerAdapter = new LikedRecyclerAdapter();
        likedRecyclerView.setLayoutManager(gridLayoutManager);
        likedRecyclerView.setAdapter(likedRecyclerAdapter);

        // When user presses the like button, will store listing unique ID into
        // individual user's likedList.
        closeButton.setOnClickListener(v -> {
            // Termintate the LikedPage activity
            finish();
        });

        // Save liked listings in sharedPrefences or SQLite
    }
}