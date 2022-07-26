package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;

public class successListPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_list_page);

        Bundle listingInfo = getIntent().getExtras();
        String pID = listingInfo.getString("pID");
        String type = listingInfo.getString("type");

        switch (type) {
            case "edit":
                TextView successHeader = findViewById(R.id.successHeader);
                TextView successLeading = findViewById(R.id.successLeading);

                successHeader.setText("Successfully edited!");
                successLeading.setText("You may now view your listing or return to homepage.");
                break;
        }

        Button viewListing = findViewById(R.id.view_listing);
        viewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle listingID = new Bundle();
                listingID.putString("lID", pID);

                Intent viewListing = new Intent(successListPage.this, individual_listing.class);
                viewListing.putExtras(listingID);
                startActivity(viewListing);
            }
        });

        Button backToHome = findViewById(R.id.back_to_home);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnhome = new Intent(view.getContext(), MainActivity.class);
                finish();
                view.getContext().startActivity(returnhome);
            }
        });
    }
}