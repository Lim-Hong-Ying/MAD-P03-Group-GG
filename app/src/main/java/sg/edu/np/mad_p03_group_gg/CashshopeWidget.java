package sg.edu.np.mad_p03_group_gg;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.net.Uri;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.User_Profile_Fragment;

/**
 * Implementation of App Widget functionality.
 */
public class CashshopeWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cashshope_widget);
        DatabaseReference mDataref;
        ArrayList<listingObject> data = new ArrayList<>();


        //views.setImageViewUri(R.id.widgetimg1, Uri.parse("https://firebasestorage.googleapis.com/v0/b/cashoppe-179d4.appspot.com/o/listing-images%2FoHZkQY1Uk0MKyEw3kN3aRLrKJwE21657456107633?alt=media&token=d4efe848-d438-40c3-9d5e-a835082a311e"));
        views.setTextViewText(R.id.newlistingnum, "5");
        AppWidgetTarget awt = new AppWidgetTarget(context, R.id.widgetimg1, views, appWidgetId) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);
            }
        };





        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser u = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDataref = database.getReference();
        Log.e("test", "test1");
        listingObject l1 = new listingObject();
        listingObject l2 = new listingObject();
        mDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("individual-listing").getChildren()) {
                    Log.e("test", "testd");
                    String getid = dataSnapshot.getKey();
                    listingObject listing = dataSnapshot.getValue(listingObject.class);
                    Log.e("item",listing.title);
                    data.add(listing);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for (listingObject l : data) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Log.e("url",l.tURL);

            if (l.getDateCreated().isAfter(l1.getDateCreated())) {
                l1 = l;
            } else if (l.getDateCreated().isAfter(l2.getDateCreated())) {
                l2 = l;
            }
        }

        //Glide.with(context)
          //      .asBitmap()
           //     .load("t")
             //   .into(awt);






        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

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

