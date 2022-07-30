package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.tools.RecursiveRadioGroup;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.paymentMethodCallback;

/**
 * TODO:
 * - Implement checking if seller has enable Stripe payment
 * - Update Firebase on User's payment method
 *
 */
public class PaymentMethodActivity extends AppCompatActivity {
    private int selectedPaymentMethodId;
    private RadioButton selectedRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);

        // Allow auto-fill of card details
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cardInputWidget.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER);
        }

        RecursiveRadioGroup paymentMethodRadios = findViewById(R.id.paymentMethodRadios);
        ImageView closeButton = findViewById(R.id.paymentMethodCloseButton);
        Button confirmButton = findViewById(R.id.confirmButton);

        RadioButton stripeRadioButton = findViewById(R.id.cardPayment);

        closeButton.setOnClickListener(view -> finish());

        stripeRadioButton.setChecked(true);

        paymentMethodRadios.setOnCheckedChangeListener((group, checkedId) -> {
            selectedPaymentMethodId = paymentMethodRadios.getCheckedItemId();
            selectedRadioButton = findViewById(selectedPaymentMethodId);

            if (selectedPaymentMethodId != -1)
            {
                if (selectedRadioButton.getText().equals("Card"))
                {
                    cardInputWidget.setVisibility(View.VISIBLE);
                }
                else
                {
                    cardInputWidget.setVisibility(View.GONE);
                }
            }
        });

        /**
         * Send intent back to CheckoutActivity containing:
         * - Payment Method (for future support of more payment methods such as Paynow)
         * - Card Information which is parsed as a PaymentMethodCreateParams object
         */
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update DB on default Payment Method


                Intent intent = new Intent();

                RadioButton selectedRadioButton = findViewById(selectedPaymentMethodId);

                if (stripeRadioButton.isChecked())
                {
                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        // Backword Forwarding Intent (Pass Data back to Checkout)
                        // put the String to pass back into an Intent and close this activity
                        intent.putExtra("paymentMethod", "Card");
                        intent.putExtra("cardInfo", params);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Check your input",
                                Toast.LENGTH_SHORT);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please select a payment method.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}