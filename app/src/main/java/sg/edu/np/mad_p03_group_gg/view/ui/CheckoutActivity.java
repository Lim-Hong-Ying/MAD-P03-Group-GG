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
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import sg.edu.np.mad_p03_group_gg.newlisting;
import sg.edu.np.mad_p03_group_gg.tools.FirebaseTools;
import sg.edu.np.mad_p03_group_gg.tools.ImageDownloader;
import sg.edu.np.mad_p03_group_gg.tools.StripeUtils;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.ConnectStripeCallback;

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
    private String sellerId;
    private String userId;
    private String stripeAccountId;
    private final StripeDialog stripeDialog = new StripeDialog(CheckoutActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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

        RadioGroup deliveryRadioGroup = findViewById(R.id.deliveryRadioGroup);
        RadioButton deliveryRadioButton = findViewById(R.id.deliveryRadioButton);
        RadioButton meetupRadioButton = findViewById(R.id.meetupRadioButton);
        ImageView changeAddressButton = findViewById(R.id.changeAddressButton);

        // Get Intent from Individual Listing Activity
        sellerId = getIntent().getStringExtra("sellerId");
        stripeAccountId = getIntent().getStringExtra("stripeAccountId");
        String productId = getIntent().getStringExtra("productId");

        // Get current user id
        auth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = auth.getCurrentUser();
        userId = fbUser.getUid();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51LKF7ZFaaAQicG0TEdtmijoaa2muufF73f7Hyhid3hXglesPpgV86ykgKWxJ74zwkrzbWa7HvrAvZExbVD5wDV1X0017hZyVPa",
                stripeAccountId
        );

        // if payment method == stripe
        // Start checkout session (create paymentIntent to get clientSecret)
        paymentLauncher = PaymentLauncher.Companion.create(
                this,
                PaymentConfiguration.getInstance(this).getPublishableKey(),
                PaymentConfiguration.getInstance(this).getStripeAccountId(),
                this::onPaymentResult
        );


        databaseReference.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    StripeUtils.getStripeAccountId(sellerId, new ConnectStripeCallback() {
                        @Override
                        public void stripeAccountIdCallback(String stripeAccountId) {
                            FirebaseTools.createListingObjectFromFirebase(productId,
                                    CheckoutActivity.this, listingObject -> {
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

                                        String customerEmail = task.getResult().child("email").getValue(String.class);
                                        startCheckout(totalPrice, customerEmail, stripeAccountId);

                                    });
                        }
                    });

                }
            }
        });

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

        changeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
     * @param totalPrice
     * @param customerEmail
     * @param stripeAccountId
     */
    private void startCheckout(int totalPrice, String customerEmail, String stripeAccountId) {

        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // Stripe uses smallest currency unit, therefore need to multiply by 100
        String json = String.format("{"
                        + "\"amount\":\"%s\","
                        + "\"cust_email\":\"%s\","
                        + "\"stripe_account\":\"%s\""
                        + "}", String.valueOf(totalPrice * 100), customerEmail,
                stripeAccountId);

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

        Button checkoutButton = findViewById(R.id.checkoutButton);

        RadioGroup radioGroup = findViewById(R.id.deliveryRadioGroup);

        // Ensure got client secret and card parameters before proceeding
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(CheckoutActivity.this,
                            "Please select one delivery method.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (paymentMethod != null)
                    {
                        if (params != null) {
                            stripeDialog.startStripeAlertDialog();
                            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                            paymentLauncher.confirm(confirmParams); // Confirm payment
                        }
                        else {
                            Toast.makeText(CheckoutActivity.this,
                                    "Please key in your payment information again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(CheckoutActivity.this,
                                "Please key in your payment information again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onPaymentResult(PaymentResult paymentResult) {
        stripeDialog.dismissDialog();
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

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (isSuccess)
                {

                    String textMessage = "Hi, a payment has been made, please confirm.";

                    //FirebaseTools.sendConfirmationMessage(userId,  sellerId, textMessage); // inform seller of a payment
                    CheckoutActivity.this.finish();
                }
            }
        });

        builder.create().show();
    }
}