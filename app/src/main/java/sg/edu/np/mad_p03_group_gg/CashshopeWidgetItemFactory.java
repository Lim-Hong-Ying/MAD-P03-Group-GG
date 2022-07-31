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

        private CountDownLatch donesignal = new CountDownLatch(5);
        private CountDownLatch finish = new CountDownLatch(1);
        private CountDownLatch finishfindingitem = new CountDownLatch(1);
    private CountDownLatch finishfindingitem1 = new CountDownLatch(1);
    private CountDownLatch FindItems = new CountDownLatch(1);
    private CountDownLatch Finishlaod2 = new CountDownLatch(1);

        private Target target;
        CashshopeWidgetItemFactory(Context context, Intent intent){
            this.context= context;
            Log.e("test", "test1");
            //Use to distinguish between different instace of app widget
            this.appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);

        }
        //Since according to widget lifecycle, when widget is called,
        // on create method will then call on datasetchange method,
        // getting data will occur in the on data set changed method
        @Override
        public void onCreate() {



        }
        //Method to update widget
    private void Intizalxzedata() throws InterruptedException{

            // Get database ref

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        Log.e("Test","Intilizepass");
        DatabaseReference mDataref = database.getReference();


        mDataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot result : snapshot.child("individual-listing").getChildren()) {
                    //get items from database

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String listingid = result.getKey();
                    String title = result.child("title").getValue(String.class);
                    long thumbnailurlsize = result.child("tURLs").getChildrenCount();
                    ArrayList<String> tURLs = new ArrayList<>();
                    // Set item pictures into a list/array
                    for (int i = 0; i < thumbnailurlsize; i++) {
                        tURLs.add(result.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                    }
                    String sellerid = result.child("sid").getValue(String.class);
                    String sellerprofilepicurl = result.child("sppu").getValue(String.class);
                    String itemcondition = result.child("iC").getValue(String.class);
                    String price = result.child("price").getValue(String.class);
                    Boolean reserved = result.child("reserved").getValue(Boolean.class);
                    String category = result.child("category").getValue(String.class);
                    String desc = result.child("description").getValue(String.class);
                    String location = result.child("location").getValue(String.class);
                    Boolean delivery = result.child("delivery").getValue(Boolean.class);
                    String deliverytype = result.child("deliveryType").getValue(String.class);
                    String deliveryprice = result.child("deliveryPrice").getValue(String.class);
                    String deliverytime = result.child("deliveryTime").getValue(String.class);
                    String TimeStamp = result.child("timeStamp").getValue(String.class);
                    Log.e("Test",FindItems.toString());
                    FindItems.countDown();//Wait for above operations to finish, reduce countdown by 1
                    Log.e("Test",FindItems.toString());
                    Log.e("Link",mDataref.child("individual-listing").child(listingid).child("tURLs").child("0").toString());
                    try {

                        FindItems.await();
                        //When countdown finditem =0 . the below code is executed, make it synchronise and prevent the views from loading beforehand
                        Log.e("Test",FindItems.toString());
                        LocalDate CurrentDate = LocalDate.now();
                        String ts = CurrentDate.toString();
                        Log.e("ComTImeStamp",ts);
                        Log.e("Timestamp",TimeStamp.toString());

                        if(TimeStamp.toString().equals(ts)) {
                            individualListingObject l = new individualListingObject(listingid, title, tURLs, sellerid, itemcondition, price, reserved, category, desc, location, delivery, deliverytype, deliveryprice, deliverytime, TimeStamp);
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

                //count down to 0 when runned
                try {
                    finish.countDown();
                    finish.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try{

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
            Log.e("Cleared",Integer.toString(llist.size()));
            //re-intialize countdown for synchronise operations
            donesignal = new CountDownLatch(5);
            finish = new CountDownLatch(1);
            finishfindingitem = new CountDownLatch(1);
            finishfindingitem1 = new CountDownLatch(1);
            FindItems = new CountDownLatch(1);
            //Clear list items if the widget is refresehed

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
            //close datasource when widget is destroyed or deleted
            llist.clear();


        }
        //Used to get the number of objects needed for the collection view(Similar to recycler view)

        @Override
        public int getCount() {



            List<listingObject> llist1 = llist;
            Log.e("Coutndown", Long.toString(donesignal.getCount()));
            try {
//when the database items has been loaded, call getcount
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


            Log.e("ViewListLength",Integer.toString(llist.size()));
            //Get remoteview
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetitem);
            //Remove any preexisting views
            views.removeAllViews(R.id.gridpics);



            //Set the title of the item
            views.setTextViewText(R.id.widgetitemtitle,llist.get(i).title);
            //Set price of collection item
            views.setTextViewText(R.id.widgetprice,"$" + llist.get(i).price);

            try {
                //Set the image for the collection item, caches it using picasso to prevent high use of bandwidth
                //furthermore, image is resize to 100 by 100 by picasso

                Bitmap b = Picasso.get().load(llist.get(i).gettURLs().get(0)).resize(100,100).get();

                views.setImageViewBitmap(R.id.cashshope_widget_picture, b);
            } catch (IOException e) {
                e.printStackTrace();
            }



            return views;
        }
        // Use default loading view when item loading from the db

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






