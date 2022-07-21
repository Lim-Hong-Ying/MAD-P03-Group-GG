package sg.edu.np.mad_p03_group_gg.tools.stripe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stripe.android.PaymentConfiguration;

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
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.databinding.ActivityConnectWithStripeBinding;

public class ConnectWithStripeActivity extends AppCompatActivity {
    private static final String BACKEND_URL = "https://cashshope.japaneast.cloudapp.azure.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private ActivityConnectWithStripeBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51LKF7ZFaaAQicG0TEdtmijoaa2muufF73f7Hyhid3hXglesPpgV86ykgKWxJ74zwkrzbWa7HvrAvZExbVD5wDV1X0017hZyVPa"
        );

        viewBinding = ActivityConnectWithStripeBinding.inflate(getLayoutInflater());

        View viewBing = viewBinding.getRoot();
        setContentView(viewBing);

        viewBinding.connectWithStripe.setOnClickListener(view -> {
            WeakReference<Activity> weakActivity = new WeakReference<>(this);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create")
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
                                    String url = responseJson.getString("url");
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    Log.d("url for stripe", url);
                                    customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        });
    }
}