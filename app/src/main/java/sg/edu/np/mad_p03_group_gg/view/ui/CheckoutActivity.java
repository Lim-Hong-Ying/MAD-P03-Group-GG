package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.stripe.android.PaymentConfiguration;

import sg.edu.np.mad_p03_group_gg.R;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51LKF7ZFaaAQicG0TEdtmijoaa2muufF73f7Hyhid3hXglesPpgV86ykgKWxJ74zwkrzbWa7HvrAvZExbVD5wDV1X0017hZyVPa"
        );

    }
}