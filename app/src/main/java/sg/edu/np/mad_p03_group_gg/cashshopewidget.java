package sg.edu.np.mad_p03_group_gg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.User_Profile_Fragment;

/**
 * Implementation of App Widget functionality.
 */
public class cashshopewidget extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "actionRefresh";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them, for loop is used
        //May be multiple widget on screen, for loop loops through widget ids to update widget in home screen


        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent serviceIntent = new Intent(context,cashshopeWidgetService.class);

            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));//Distinguish between different instances
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.cashshope_widget);
            //Set remote adapter
            views.setRemoteAdapter(R.id.gridpics,serviceIntent);

           Intent clickIntent = new Intent(context,cashshopewidget.class);//Sub class of broadcast, if Action_App_Widge_update call, a update will run
           clickIntent.setAction(ACTION_REFRESH);
           clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[i]);
            // create pending intenet(For onclick button)
           PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,appWidgetIds[i],clickIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            views.setEmptyView(R.id.gridpics,R.id.cashshope_widgetempty);
            // set intent to button
            views.setOnClickPendingIntent(R.id.refreshbutton,clickPendingIntent);
            //Update app widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);


            Log.e("Test","providerd entered");








        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);


    }
    // Method to receive broadcast from pending intent

    @Override
    public void onReceive(Context context, Intent intent) {
        //When broadcast from pending intent is received, get intenet and get intent action and check if action equals to intent action
        //If it is, referesh items
        if(ACTION_REFRESH.equals(intent.getAction())) {
            Log.e("Actionrefresh", "providerd entered");
            //get app widget ID
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.e("ID", Integer.toString(appWidgetId));
            //Get app widget manger for the app widget ID
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //Get app widget remote view
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cashshope_widget);

            //This function runs the On DataSet changed method in the Cashshopewidgetitem factory
            //Which will call all the other methods necessary(refer to android developers docs on widgets)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridpics);
        }

            super.onReceive(context, intent);
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmap;

        public ImageDownloader(ImageView bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap image = null;
            try {
                InputStream input = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(input);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            bitmap.setImageBitmap(result);
        }
    }


}