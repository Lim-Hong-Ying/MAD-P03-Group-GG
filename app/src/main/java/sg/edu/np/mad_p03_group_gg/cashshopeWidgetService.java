package sg.edu.np.mad_p03_group_gg;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class cashshopeWidgetService extends RemoteViewsService {
    @Override
    //Remoteviewfactory class are like adapters in recycler view
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("test", "test1");
        return new CashshopeWidgetItemFactory(getApplicationContext(),intent);// returns the cashshopeitemviewfactory class
    }
    //Implent remoteviewsfactory
      class CashshopeWidgetItemFactory implements RemoteViewsFactory {
        private Context context;
        private int appWidgetId;
        private List<listingObject> llist = new ArrayList<listingObject>();
        //private String[] exampleData = {"one", "two", "three", "four",
            //    "five", "six", "seven", "eight", "nine", "ten"};


        private Target target;
        CashshopeWidgetItemFactory(Context context, Intent intent){
            this.context= context;
            Log.e("test", "test1");
            //Use to distinguish between different instace of app widget
            this.appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);

        }
        //Connect to data source here
        @Override
        public void onCreate() {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mDataref = database.getReference();
            mDataref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot result : snapshot.child("individual-listing").getChildren()) {

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String listingid = result.getKey();
                        String title = result.child("title").getValue(String.class);
                        String thumbnailurl = result.child("tURLs").child("0").getValue(String.class);
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
                        Log.e("Link",mDataref.child("individual-listing").child(listingid).child("tURLs").child("0").toString());
                        individualListingObject l = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime,TimeStamp);
                        LocalDate CurrentDate = LocalDate.now();
                        String ts = CurrentDate.toString();
                        if(l.getTimeStamp().equals(ts)){
                            llist.add(l);
                        }

                        Log.e("indata", Integer.toString(llist.size()));





                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        //Update widget




        @Override
        public void onDataSetChanged() {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mDataref = database.getReference();
            Log.e("test", "test1");
            Log.e("DS", "Dataset changed");



            mDataref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot result : snapshot.child("individual-listing").getChildren()) {

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String listingid = result.getKey();
                        String title = result.child("title").getValue(String.class);
                        String thumbnailurl = result.child("tURLs").child("0").getValue(String.class);
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
                        Log.e("Link",mDataref.child("individual-listing").child(listingid).child("tURLs").child("0").toString());












                        individualListingObject l = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime,TimeStamp);
                      //  llist.add(l);
                        //Log.e("Provider",llist.get(0).title);
                        Log.e("PicLink",l.tURL.toString());



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        @Override
        public void onDestroy() {
            //close datasource


        }

        @Override
        public int getCount() {


            Log.e("count",Integer.toString(llist.size()));


            return llist.size();
        }
        // COmbine object to views

        @Override
        public RemoteViews getViewAt(int i) {
            Log.e("Test1","getviewat");
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetitem);
            Log.e("Test1","provider enter");
            Intent fillIntent = new Intent();
            fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setOnClickFillInIntent(R.id.cashshope_widget_picture,fillIntent);
            views.setTextViewText(R.id.widgetitemtitle,llist.get(i).title);


//            AppWidgetTarget awt2 = new AppWidgetTarget(context, R.id.cashshope_widget_picture, views, appWidgetId) {
//
//            };
//
//            Glide.with(context)
//                    .asBitmap()
//                    .load(llist.get(i).tURL)
//                    .into(awt2);
            views.setTextViewText(R.id.widgetprice,"$" + llist.get(i).price);
            try {
                URL url = new URL(llist.get(i).tURL);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                views.setImageViewBitmap(R.id.cashshope_widget_picture,image);
                //Log.e("Test",Integer.toString(llist.size()));
            } catch(IOException e) {
                Log.e("No Image", "No Image found/something went wrong");
            }
//            target=new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            }
//            Handler uiHandler = new Handler(Looper.getMainLooper());
//            uiHandler.post(() -> {
//            Picasso.get()
//                    .load(llist.get(i).tURL)
//                    .into(views, R.id.cashshope_widget_picture, new int[] {appWidgetId});
//            });



            return views;
        }

        @Override
        public RemoteViews getLoadingView() {

            return null;
        }
        //Number of views used in the widget collection
        @Override
        public int getViewTypeCount() {

            return 1;
        }

//set item ids as position
        @Override
        public long getItemId(int i) {
            Log.e("getItem",Integer.toString(i));

            return i;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }
        public listingObject [] RetriveList(){
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mDataref = database.getReference();
            List<listingObject> llist = new ArrayList<listingObject>();
            Log.e("test", "test1");

            mDataref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot result : snapshot.child("individual-listing").getChildren()) {

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String listingid = result.getKey();
                        String title = result.child("title").getValue(String.class);
                        String thumbnailurl = result.child("tURLs").child("0").getValue(String.class);
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
                        Log.e("Link",mDataref.child("individual-listing").child(listingid).child("tURLs").child("0").toString());
                        individualListingObject l = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime,TimeStamp);
                        llist.add(l);
                        Log.e("indata", Integer.toString(llist.size()));



                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            Log.e("Retrive from firebase",Integer.toString(llist.size()));
            listingObject [] larray = llist.toArray(new listingObject[0]);

            return larray;
        }



    }

    }





