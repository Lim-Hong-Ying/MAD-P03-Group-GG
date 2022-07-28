package sg.edu.np.mad_p03_group_gg.view.userConsent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import sg.edu.np.mad_p03_group_gg.R;

public class TermsAndConditions extends AppCompatActivity {
    private final Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        Button acceptButton = findViewById(R.id.privacyAcceptButton);
        Button declineButton = findViewById(R.id.privacyDeclineButton);

        WebView webView = findViewById(R.id.privacyWebView);
        webView.loadUrl("https://cashshope.japaneast.cloudapp.azure.com/tnc");

        /**
         * Send intent back to CheckoutActivity
         */
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update DB on default Payment Method

                // Backword Forwarding Intent (Pass Data back to Checkout)
                // put the String to pass back into an Intent and close this activity
                intent.putExtra("isAgree", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update DB on default Payment Method

                // Backword Forwarding Intent (Pass Data back to Checkout)
                // put the String to pass back into an Intent and close this activity
                intent.putExtra("isAgree", "false");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Remember the user's press of the back key action
        intent.putExtra("isAgree", "false");
        setResult(RESULT_OK, intent);
        finish();
        // Call the super's method
        super.onBackPressed();
    }
}