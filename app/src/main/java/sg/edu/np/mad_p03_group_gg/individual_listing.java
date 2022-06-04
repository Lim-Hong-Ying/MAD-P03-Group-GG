package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class individual_listing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_listing);

        TextView title;
        TextView price;
        TextView itemcondition;
        TextView description;
        TextView location;
        TextView deliveryoption;
        TextView deliveryprice;
        TextView deliverytime;

        title = findViewById(R.id.individual_title);
        price = findViewById(R.id.individual_price);
        itemcondition = findViewById(R.id.individual_itemcondition);
        description = findViewById(R.id.individual_description);
        location = findViewById(R.id.individual_salelocation);
        deliveryoption = findViewById(R.id.individual_deliveryoption);
        deliveryprice = findViewById(R.id.individual_deliveryprice);
        deliverytime = findViewById(R.id.individual_deliverytime);
    }
}