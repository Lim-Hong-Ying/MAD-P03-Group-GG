package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stripe.android.PaymentConfiguration;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.individualListingObject;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.Callback;

public class CheckoutActivity extends AppCompatActivity {
    private static individualListingObject individuallistingObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51LKF7ZFaaAQicG0TEdtmijoaa2muufF73f7Hyhid3hXglesPpgV86ykgKWxJ74zwkrzbWa7HvrAvZExbVD5wDV1X0017hZyVPa"
        );

        // Get Intent from Individual Listing Activity
        String sellerId = getIntent().getStringExtra("sellerId");
        String productId = getIntent().getStringExtra("productId");

        ImageView closeButton = findViewById(R.id.checkoutPageCloseButton);
        TextView listingTitleView = findViewById(R.id.listingTitle);
        TextView listingPriceView = findViewById(R.id.listingPrice);
        TextView listingTitlePriceCard = findViewById(R.id.listingTitlePriceCard);
        TextView listingCostPriceCard = findViewById(R.id.listingCostPriceCard);
        TextView deliveryCostTextView = findViewById(R.id.deliveryCostTextView);
        TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);
        ImageView paymentMethodLogo = findViewById(R.id.paymentMethodLogo);
        TextView paymentDetailsHint = findViewById(R.id.paymentDetailsHint);
        TextView changePaymentButton = findViewById(R.id.changePaymentButton);
        LinearLayout addressLayout = findViewById(R.id.addressLayout);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseTools.createListingObjectFromFirebase(productId, this, new Callback() {
            @Override
            public void listingObjectCallback(individualListingObject listingObject) {
                listingTitleView.setText(listingObject.getTitle());
                listingPriceView.setText(listingObject.getPrice());
            }
        });
    }
}