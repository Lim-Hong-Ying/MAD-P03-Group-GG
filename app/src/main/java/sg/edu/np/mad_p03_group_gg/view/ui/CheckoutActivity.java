package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.payments.paymentlauncher.PaymentLauncher;
import com.stripe.android.payments.paymentlauncher.PaymentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.individualListingObject;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.tools.ImageDownloader;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.Callback;

public class CheckoutActivity extends AppCompatActivity {
    private static final String BACKEND_URL = "https://cashshope.japaneast.cloudapp.azure.com/";
    private String paymentIntentClientSecret;
    private PaymentLauncher paymentLauncher;
    private OkHttpClient httpClient = new OkHttpClient();
    private PaymentMethodCreateParams params;
    private String paymentMethod;

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

        ImageView closeButton = findViewById(R.id.paymentMethodCloseButton);
        TextView listingTitleView = findViewById(R.id.listingTitle);
        TextView listingPriceView = findViewById(R.id.listingPrice);
        ImageView listingPictureView = findViewById(R.id.listingPictureView);
        TextView listingTitlePriceCard = findViewById(R.id.listingTitlePriceCard);
        TextView listingCostPriceCard = findViewById(R.id.listingCostPriceCard);
        TextView deliveryCostTextView = findViewById(R.id.deliveryCostTextView);
        TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);
        ImageView paymentMethodLogo = findViewById(R.id.paymentMethodLogo);
        TextView paymentDetailsHint = findViewById(R.id.paymentDetailsHint);
        TextView changePaymentButton = findViewById(R.id.changePaymentButton);
        LinearLayout addressLayout = findViewById(R.id.addressLayout);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseTools.createListingObjectFromFirebase(productId, this, listingObject -> {
            listingTitleView.setText(listingObject.getTitle());
            listingPriceView.setText("$" + listingObject.getPrice());
            new ImageDownloader(listingPictureView).execute(listingObject.gettURL());
            listingTitlePriceCard.setText(listingObject.getTitle());
            listingCostPriceCard.setText("$" + listingObject.getPrice());
            int totalPrice = 0;
            if (listingObject.getDeliveryPrice().equals("")) {
                deliveryCostTextView.setText("$0");
                totalPrice =  Integer.parseInt(listingObject.getPrice()) + 0;
            } else {
                totalPrice = Integer.parseInt(listingObject.getPrice()) +
                        Integer.parseInt(listingObject.getDeliveryPrice());
            }
            totalPriceTextView.setText("$" + totalPrice);
        });

        /**
         * TO-DO:
         *
         * Check if user already has payment method
         *
         * If have, use that one, (e.g. retrieve CC info from Firebase)
         */

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        paymentMethod = data.getStringExtra("paymentMethod");

                        if (paymentMethod.equals("Card"))
                        {
                            params = data.getParcelableExtra("cardInfo");

                        }
                    }
                });

        changePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent paymentMethodIntent = new Intent(CheckoutActivity.this,
                        PaymentMethodActivity.class);
                activityResultLauncher.launch(paymentMethodIntent);
            }
        });

        // Check which payment method selected and do appropriate functions
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentMethod.equals("Card"))
                {
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params,
                                    paymentIntentClientSecret);
                /*paymentLauncher = PaymentLauncher.Companion.create(
                        "PaymentResult: ",
                        PaymentConfiguration.getInstance(this.getApplicationContext()).getPublishableKey(),
                        PaymentConfiguration.getInstance(this.getApplicationContext()).getStripeAccountId(),
                        this::onPaymentResult
                );*/
                    paymentLauncher.confirm(confirmParams);
                }
                else if (paymentMethod.equals("Paynow"))
                {
                    // If it's Paynow, launch Paynow Activity
                }
                else
                {
                    // If it's Cardano, launch Cardano Activity
                }
            }
        });

    }

    private void startCheckout(int listingTotalPrice) {
        // Request a PaymentIntent from your server and store its client secret in paymentIntentClientSecret

        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // Stripe uses smallest currency unit
        String json = String.format("{"
                + "\"amount\":\"%s\","
                + "\"items\":["
                + "{\"id\":\"photo_subscription\"}"
                + "]"
                + "}", String.valueOf(listingTotalPrice).concat("00"));

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();

        httpClient.newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Request failed
                        Log.d("Error", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(body);
                            paymentIntentClientSecret = responseJson.getString("client_secret");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void onPaymentResult(PaymentResult paymentResult) {
        String message = "";
        if (paymentResult instanceof PaymentResult.Completed) {
            message = "Completed!";
        } else if (paymentResult instanceof PaymentResult.Canceled) {
            message = "Canceled!";
        } else if (paymentResult instanceof PaymentResult.Failed) {
            message = "Failed: " + ((PaymentResult.Failed) paymentResult).getThrowable().getMessage();
        }

        displayAlert("PaymentResult: ", message);
    }

    private void displayAlert(@NonNull String title, @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", null);

        builder.create().show();
    }
}