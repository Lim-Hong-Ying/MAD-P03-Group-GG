package sg.edu.np.mad_p03_group_gg.view.userConsent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import sg.edu.np.mad_p03_group_gg.R;

public class PrivacyPolicy extends AppCompatActivity {
    private final Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Button acceptButton = findViewById(R.id.privacyAcceptButton);
        Button declineButton = findViewById(R.id.privacyDeclineButton);

        WebView webView = findViewById(R.id.privacyWebView);

        // Prevent webpage from redirecting user out of the activity's webview
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                //True if the host application wants to leave the current WebView and handle the
                // url itself, otherwise return false.
                return false;
            }
        });
        webView.loadUrl("https://westwq.github.io/MADPrivacy");

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

    /**
     * Handles back button event
     *
     * If user did not press decline but press back button, return false regardless, as user
     * did not conset to the agreements
     */
    @Override
    public void onBackPressed() {
        intent.putExtra("isAgree", "false");
        setResult(RESULT_OK, intent);
        finish();
        // Call the super's method
        super.onBackPressed();
    }

}