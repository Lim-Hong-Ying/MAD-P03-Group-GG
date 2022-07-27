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
import android.widget.Toast;

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
import java.util.concurrent.CountDownLatch;


//Implent remoteviewsfactory
    class CashshopeWidgetItemFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;
        private int appWidgetId;
        private ArrayList<listingObject> llist = new ArrayList<listingObject>();
        //private String[] exampleData = {"one", "two", "three", "four",
        //    "five", "six", "seven", "eight", "nine", "ten"};
        private CountDownLatch donesignal = new CountDownLatch(5);
        private CountDownLatch finish = new CountDownLatch(1);
        private CountDownLatch finishfindingitem = new CountDownLatch(1);
    private CountDownLatch finishfindingitem1 = new CountDownLatch(1);
    private CountDownLatch FindItems = new CountDownLatch(1);


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




        }
        //Update widget
    private void Intizalxzedata() throws InterruptedException{
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
                    FindItems.countDown();
                    Log.e("Link",mDataref.child("individual-listing").child(listingid).child("tURLs").child("0").toString());
                    try {
                        FindItems.await();
                        LocalDate CurrentDate = LocalDate.now();
                        String ts = CurrentDate.toString();
                        Log.e("ComTImeStamp",ts);
                        Log.e("Timestamp",TimeStamp.toString());

                        if(TimeStamp.toString().equals(ts)) {
                            individualListingObject l = new individualListingObject(listingid, title, thumbnailurl, sellerid, sellerprofilepicurl, itemcondition, price, reserved, desc, location, delivery, deliverytype, deliveryprice, deliverytime, TimeStamp);
                            llist.add(l);
                        }



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    Log.e("indata", Integer.toString(llist.size()));
                    try {



                        Log.e("List",Integer.toString(llist.size()));
                    }
                    catch(Exception e){
                        Toast.makeText(context,
                                "Something went wrong, please remove and reload the widget",Toast.LENGTH_SHORT).show();
                    }





                }

                finish.countDown();//count down to 0 when runned
                try{
                    finish.await();
                    Log.e("PassAwait","Pass");
                    Log.e("Passawaitlistlentgh",Integer.toString(llist.size()));




                    //finishfindingitem1.countDown();
                Log.e("ff1",finishfindingitem1.toString());
                }
                catch (Exception e){
                    Toast.makeText(context,
                            "Something went wrong, please remove and reload the widget",Toast.LENGTH_SHORT).show();
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




        @Override
        public void onDataSetChanged() {
            llist.clear();
            Log.e("Cleared",Integer.toString(llist.size()));
            try {

                Intizalxzedata();
            }
            catch(Exception e){
                Toast.makeText(context,
                        "Something went wrong, please remove and reload the widget",Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onDestroy() {
            //close datasource
            llist.clear();


        }

        @Override
        public int getCount() {



            List<listingObject> llist1 = llist;
            Log.e("Coutndown", Long.toString(donesignal.getCount()));
            try {

                finish.await();
                Log.e("TodayCount",Integer.toString(llist1.size()));
                return llist1.size();
            } catch (Exception e) {
                return llist1.size();


            }

        }
        // COmbine object to views

        @Override
        public RemoteViews getViewAt(int i) {

            Log.e("Test1","getviewat");
            Log.e("ViewListLength",Integer.toString(llist.size()));
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetitem);
            views.removeAllViews(R.id.gridpics);

            Log.e("Test1","provider enter");
            Intent fillIntent = new Intent();
            fillIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            views.setOnClickFillInIntent(R.id.cashshope_widget_picture,fillIntent);
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




    }






