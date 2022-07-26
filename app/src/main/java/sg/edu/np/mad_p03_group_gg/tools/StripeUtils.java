package sg.edu.np.mad_p03_group_gg.tools;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.payments.paymentlauncher.PaymentLauncher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sg.edu.np.mad_p03_group_gg.newlisting;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.ConnectStripeCallback;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.OnboardStatusCallback;
import sg.edu.np.mad_p03_group_gg.view.ui.CheckoutActivity;
import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;
import sg.edu.np.mad_p03_group_gg.view.ui.StripeDialog;
import sg.edu.np.mad_p03_group_gg.view.ui.fragments.User_Profile_Fragment;

public class StripeUtils {
    private static final String BACKEND_URL = "https://cashshope.japaneast.cloudapp.azure.com/";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private static DatabaseReference databaseReference = database.getReference();
    private static OkHttpClient httpClient = new OkHttpClient();

    /**
     * Generates a new account and redirects users to Stripe's environment for
     * onboarding.
     *
     * Only use when user is new (i.e. do not already have an account number)
     * Check the existance of a Stripe account ID using getStripeAccountId()
     *
     * @param stripeDialog
     * @param newlistingActivity
     * @param currentUserId
     */
    public static void onboardUser(StripeDialog stripeDialog, newlisting newlistingActivity,
                             String currentUserId) {
        newlistingActivity.runOnUiThread(() -> {
            // This is where your UI code goes.
            stripeDialog.startStripeAlertDialog();
        });

        WeakReference<Activity> weakActivity = new WeakReference<>(newlistingActivity);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "onboard")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ""))
                .build();
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        // Request failed
                        Log.d("Error", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        final Activity activity = weakActivity.get();
                        if (activity == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            // Request failed
                        } else {
                            String body = response.body().string();
                            try {
                                JSONObject responseJson = new JSONObject(body);
                                String stripeAccountId = responseJson.getString("account_id");
                                String url = responseJson.getJSONObject("url").getString("url");

                                // Set stripeAccountId
                                databaseReference
                                        .child("users")
                                        .child(currentUserId).child("stripeAccountId")
                                        .setValue(stripeAccountId);

                                newlistingActivity.runOnUiThread(() -> {
                                            // This is where your UI code goes.
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    stripeDialog.dismissDialog();
                                    customTabsIntent.launchUrl(newlistingActivity, Uri.parse(url));
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * Check the Onboarding Status of a Stripe account (for sellers).
     *
     * Determine if an account has successfully completed the onboarding format by calling Stripe's
     * Accounts API. If a user's payout feaature is not enabled, the onboarding process is deemed
     * to be incomplete.
     *
     * @param stripeAccountId
     * @param onboardStatusCallback
     */
    public static void onboardStatus(String stripeAccountId,
                                        final OnboardStatusCallback onboardStatusCallback) {
        // Make request to server's end point to request on account information
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String json = String.format("{"
                + "\"account_id\":\"%s\""
                + "}", stripeAccountId);

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "account")
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

                            // Check if payouts_enabled
                            Boolean payoutsEnabled = responseJson.getBoolean("payouts_enabled");

                            // If payouts not enabled, assume not onboarded
                            // as without payouts, seller cannot receive money from Stripe
                            onboardStatusCallback.isOnboardCallback(payoutsEnabled);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Resume the onboarding process by using back the user's StripeAccountId, no new account
     * will be generated. The user will be redirected to fill in the required information to
     * compelte the onboarding process.
     *
     * @param stripeDialog
     * @param newlistingActivity
     * @param stripeAccountId
     */
    public static void resumeOnboard(StripeDialog stripeDialog, newlisting newlistingActivity,
                                   String stripeAccountId) {
        newlistingActivity.runOnUiThread(() -> {
            // This is where your UI code goes.
            stripeDialog.startStripeAlertDialog();
        });

        // Make request to server's end point to request on account information
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String json = String.format("{"
                + "\"account_id\":\"%s\""
                + "}", stripeAccountId);

        RequestBody body = RequestBody.create(mediaType, json);

        WeakReference<Activity> weakActivity = new WeakReference<>(newlistingActivity);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "reauth")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        // Request failed
                        Log.d("Error", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        final Activity activity = weakActivity.get();
                        if (activity == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            // Request failed
                        } else {
                            String body = response.body().string();
                            try {
                                JSONObject responseJson = new JSONObject(body);
                                String url = responseJson.getJSONObject("url").getString("url");

                                newlistingActivity.runOnUiThread(() -> {
                                    // This is where your UI code goes.
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    stripeDialog.dismissDialog();
                                    customTabsIntent.launchUrl(newlistingActivity, Uri.parse(url));
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * Get a user's StripeAccountId from Firebase
     *
     * @param sellerId
     * @param connectStripeCallback
     */
    public static void getStripeAccountId(String sellerId, final ConnectStripeCallback connectStripeCallback) {
        databaseReference.child("users").child(sellerId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            String stripeAccountId = task.getResult().child("stripeAccountId")
                                    .getValue(String.class);
                            connectStripeCallback.stripeAccountIdCallback(stripeAccountId);
                        }
                    }
                });
    }

    public static void createDashboardLink(StripeDialog stripeDialog, FragmentActivity fragmentActivity,
                                           String stripeAccountId)
    {
        fragmentActivity.runOnUiThread(() -> {
            // This is where your UI code goes.
            stripeDialog.startStripeAlertDialog();
        });

        // Make request to server's end point to request on account information
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String json = String.format("{"
                + "\"account_id\":\"%s\""
                + "}", stripeAccountId);

        RequestBody body = RequestBody.create(mediaType, json);

        WeakReference<Activity> weakActivity = new WeakReference<>(fragmentActivity);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "dashboard")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        // Request failed
                        Log.d("Error", e.getMessage());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        final Activity activity = weakActivity.get();
                        if (activity == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            // Request failed
                        } else {
                            String body = response.body().string();
                            try {
                                JSONObject responseJson = new JSONObject(body);
                                String url = responseJson.getString("url");
                                fragmentActivity.runOnUiThread(() -> {
                                    // This is where your UI code goes.
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    stripeDialog.dismissDialog();
                                    customTabsIntent.launchUrl(fragmentActivity, Uri.parse(url));
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public static void confirmPayment(PaymentMethodCreateParams params, StripeDialog stripeDialog,
                                      String paymentIntentClientSecret, PaymentLauncher paymentLauncher,
                                      Context context)
    {
        if (params != null) {
            stripeDialog.startStripeAlertDialog();
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            paymentLauncher.confirm(confirmParams); // Confirm payment
        } else {
            Toast.makeText(context,
                    "Please key in your payment information again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
