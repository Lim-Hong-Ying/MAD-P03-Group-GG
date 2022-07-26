package sg.edu.np.mad_p03_group_gg.view.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import sg.edu.np.mad_p03_group_gg.R;

public class StripeDialog {
    private Activity activity;
    private AlertDialog dialog;

    public StripeDialog(Activity activity) {
        this.activity = activity;
    }

    public void startStripeAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_stripe, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
