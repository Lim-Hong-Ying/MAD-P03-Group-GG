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
public class CashshopeWidget extends AppWidgetProvider {
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cashshope_widget);
        DatabaseReference mDataref;
        ArrayList<individualListingObject> data = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser u = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDataref = database.getReference();
        Log.e("test", "test1");

        mDataref.addValueEventListener(new ValueEventListener() {
            individualListingObject l1 = new individualListingObject();
            individualListingObject l2 = new individualListingObject();
            int NoofNewlisting = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot result : snapshot.child("individual-listing").getChildren()) {
                    Log.e("test", "testd");


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String listingid = result.getKey();
                    String title = result.child("title").getValue(String.class);
                    String thumbnailurl = result.child("tURL").getValue(String.class);
                    String sellerid = result.child("sid").getValue(String.class);
                    String sellerprofilepicurl = result.child("sppu").getValue(String.class);
                    String itemcondition = result.child("iC").getValue(String.class);
                    String price = result.child("price").getValue(String.class);
                    Boolean reserved = result.child("reserved").getValue(Boolean.class);
                    String desc = result.child("description").getValue(String.class);
                    String location = result.child("location").getValue(String.class);
                    Boolean delivery = result.child("delivery").getValue(Boolean.class);
                    String deliverytype = result.child("deliveryType").getValue(String.class);
                    String deliveryprice = result.child("deliveryPrice").getValue(String.class);
                    String deliverytime = result.child("deliveryTime").getValue(String.class);
                    String TimeStamp = result.child("timeStamp").getValue(String.class);




                    individualListingObject l = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime,TimeStamp);




                    if (l.getTimeStamp()==null){
                        String Time = LocalDate.now().toString();
                        l.setTimeStamp(Time);
                    }
                    Log.e("Test",l.getTimeStamp());
                    l1.setTimeStamp("1980-01-01");
                    l2.setTimeStamp("1980-01-01");
                    if(LocalDate.parse(l.getTimeStamp()).isEqual(LocalDate.now())){
                        NoofNewlisting+=1;

                    }
                    views.setTextViewText(R.id.newlistingnum, Integer.toString(NoofNewlisting)+" new arrivals");





                    if ((LocalDate.parse(l.getTimeStamp())).isAfter(LocalDate.parse(l1.getTimeStamp()))) {
                        l1 = l;
                    } else if (LocalDate.parse(l.getTimeStamp()).isAfter(LocalDate.parse(l2.getTimeStamp()))) {
                        l2 = l;
                    }

                    Log.e("item",l.title);
                    data.add(l);
                }

                Log.e("List Length",Integer.toString(data.size()));
                if(l1.sID!=null) {
                    AppWidgetTarget awt = new AppWidgetTarget(context, R.id.widgetimg1, views, appWidgetId) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide.with(context)
                            .asBitmap()
                            .load(l1.tURL)
                            .into(awt);
                }
                if(l2.sID!=null){
                    AppWidgetTarget awt2 = new AppWidgetTarget(context, R.id.widgetimg1, views, appWidgetId) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    Glide.with(context)
                            .asBitmap()
                            .load(l2.tURL)
                            .into(awt2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });










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

