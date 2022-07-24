package sg.edu.np.mad_p03_group_gg.view.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
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
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.tools.ImageDownloader;

/**
 * TODO:
 * 1) If user payment method is Card, prompt to enter card info again.
 */
public class CheckoutActivity extends AppCompatActivity {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private static DatabaseReference databaseReference = database.getReference();
    private static final String BACKEND_URL = "https://cashshope.japaneast.cloudapp.azure.com/";
    private String paymentIntentClientSecret;
    private PaymentLauncher paymentLauncher;
    private OkHttpClient httpClient = new OkHttpClient();
    private PaymentMethodCreateParams params;
    private String paymentMethod;
    private FirebaseAuth auth;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51LKF7ZFaaAQicG0TEdtmijoaa2muufF73f7Hyhid3hXglesPpgV86ykgKWxJ74zwkrzbWa7HvrAvZExbVD5wDV1X0017hZyVPa",
                "acct_1LNubg2X820yavlY"
        );

        /**
         * TODO: Get Connected Stripe Account (Seller Account) ID from Firebase
         * Get seller Id from listingObject,
         * Then get onboarding id from User on Firebase
         *
         */

        stripe = new Stripe(
                this,
                PaymentConfiguration.getInstance(this).getPublishableKey(),
                PaymentConfiguration.getInstance(this).getStripeAccountId()
        );

        // if payment method == stripe
        // Start checkout session (create paymentIntent to get clientSecret)
        paymentLauncher = PaymentLauncher.Companion.create(
                this,
                PaymentConfiguration.getInstance(this).getPublishableKey(),
                PaymentConfiguration.getInstance(this).getStripeAccountId(),
                this::onPaymentResult
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
        RadioButton deliveryRadioButton = findViewById(R.id.deliveryRadioButton);
        RadioButton meetupRadioButton = findViewById(R.id.meetupRadioButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseTools.createListingObjectFromFirebase(productId, this, listingObject -> {
            listingTitleView.setText(listingObject.getTitle());
            listingPriceView.setText("$" + listingObject.getPrice());

            new ImageDownloader(listingPictureView).execute(listingObject.gettURLs().get(0));

            listingTitlePriceCard.setText(listingObject.getTitle());
            listingCostPriceCard.setText("$" + listingObject.getPrice());
            int totalPrice = 0;
            if (listingObject.getDeliveryPrice().equals("")) {
                deliveryCostTextView.setText("$0");
                totalPrice =  Integer.parseInt(listingObject.getPrice()) + 0;
            } else {
                deliveryCostTextView.setText("$" + listingObject.getDeliveryPrice());
                totalPrice = Integer.parseInt(listingObject.getPrice()) +
                        Integer.parseInt(listingObject.getDeliveryPrice());
            }
            totalPriceTextView.setText("$" + totalPrice);

            // If seller did not enable delivery, set view to GONE
            if (listingObject.getDelivery() == false)
            {
                deliveryRadioButton.setVisibility(View.GONE);
                addressLayout.setVisibility(View.GONE);
                meetupRadioButton.setChecked(true); // check the meetup option
            }

            startCheckout(totalPrice);
        });

        /**
         * TO-DO:
         *
         * Check if user already has payment method
         *
         * If have, use that one, (e.g. retrieve CC info from Firebase)
         */

        // Get result from PaymentMethod Activity
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
                            // Set last 4 digits of card (allow user to double check)
                            paymentDetailsHint.setText("***" + params.getCard().getLast4());
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
                    // If stripe do nothing, as it is handled by startCheckout()
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

    /**
     * Only for Stripe Payment
     *
     * 1) Gets email address of current authenticated user
     * 2) Send POST request to backend to create payment intent
     * 3) Get client secret from payment intent
     *
     * @param listingTotalPrice
     */
    private void startCheckout(int listingTotalPrice) {

        // Get current user id
        auth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = auth.getCurrentUser();
        String currentUserID = fbUser.getUid();

        databaseReference.child("users").child(currentUserID).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String customerEmail = task.getResult().child("email").getValue(String.class);

                    // Request a PaymentIntent from your server and store its client secret in paymentIntentClientSecret

                    // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

                    // Stripe uses smallest currency unit
                    String json = String.format("{"
                            + "\"amount\":\"%s\","
                            + "\"cust_email\":\"%s\","
                            + "\"stripe_account\":\"%s\""
                            + "}", String.valueOf(listingTotalPrice).concat("00"), customerEmail,
                            "acct_1LNubg2X820yavlY");

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
                                        Log.d("ClientSecret", paymentIntentClientSecret);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        });

        Button checkoutButton = findViewById(R.id.checkoutButton);

        // Check which payment method selected and do appropriate functions
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentMethod.equals("Card"))
                {
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        paymentLauncher.confirm(confirmParams); // Confirm payment

                    }
                }
            }
        });
    }

    private void onPaymentResult(PaymentResult paymentResult) {
        String message = "";
        Boolean isSuccess = false;

        if (paymentResult instanceof PaymentResult.Completed) {
            message = "Completed!";
            isSuccess = true;
        } else if (paymentResult instanceof PaymentResult.Canceled) {
            message = "Canceled!";
        } else if (paymentResult instanceof PaymentResult.Failed) {
            message = "Failed: " + ((PaymentResult.Failed) paymentResult).getThrowable().getMessage();
        }
        displayAlert("PaymentResult: ", message, isSuccess);
    }

    private void displayAlert(@NonNull String title, @Nullable String message, Boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        if (isSuccess)
        {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CheckoutActivity.this.finish();
                }
            });
        }

        builder.create().show();
    }
}