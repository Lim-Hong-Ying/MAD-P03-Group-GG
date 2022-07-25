package sg.edu.np.mad_p03_group_gg;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;


public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
   private ArrayList<Event> eventModelArrayList = new ArrayList<>();
   private String userId = HomepageFragment.userId;
   private Context context;

   public EventsRecyclerViewAdapter(ArrayList<Event> eventModelArrayList, Context context) {
       this.eventModelArrayList = eventModelArrayList;
       this.context = context;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
       return new ViewHolder(cardView);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // Get selected event
       Event e = Event.eventsList.get(position);
       // Get eventID of selected event
       int id = e.getID();
       // Get date of selected event
       String date = e.getDate().toString();
       // Split date string into year month day. First index is year, second is month, third is day
       String[] dateArr = date.split("-", 3);
       // Get month name from month number
       int monthNo = Integer.parseInt(dateArr[1]);
       holder.month.setText(getMonthName(monthNo));
       // Set day of event
       holder.date.setText(dateArr[2]);
       // Set event name, location
       holder.title.setText(e.getName());
       holder.place.setText(e.getLocation());
       holder.time.setText(e.getTime());
       // When event is clicked, navigate to EventDetails page
       holder.event_card.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               Intent createEvent = new Intent(view.getContext(), EventDetails.class);
               // Sends data for EventDetails page to initialise current selected event details
               createEvent.putExtra("EventDetails", id);
               view.getContext().startActivity(createEvent);
           }
       });
       // When menu is clicked, choose between edit or create event
       holder.menu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
               popupMenu.inflate(R.menu.popup_menu);
               popupMenu.show();
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           // If edit event is clicked, navigate to EventDetails page
                           case R.id.edit:
                               Intent editEvent = new Intent(view.getContext(), EventDetails.class);
                               // Sends data for EventDetails page to initialise current selected event details
                               editEvent.putExtra("EventDetails", id);
                               // Sends data to allow inputs to be editable in EventDetails page
                               editEvent.putExtra("Editable", true);
                               view.getContext().startActivity(editEvent);
                               return true;
                               // If delete event is clicked, remove event from list, firebase, google calendar
                           case R.id.delete:
                               Event.eventsList.remove(e);
                               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                               userId = user.getUid();
                               EventEditActivity.removeDataFromFireBase(userId, id);
                               notifyDataSetChanged();
                               EventsPage.noOfEvent(Event.eventsList);
                               try{DeleteCalendarEntry(ListSelectedCalendars(e.getName()));}catch (Exception e){Toast.makeText(view.getContext(), "Allow permissions to sync with Google Calendar", Toast.LENGTH_LONG).show();}
                               Toast.makeText(view.getContext(), "Event Deleted!", Toast.LENGTH_SHORT).show();
                               return true;
                           default:
                               return false;
                       }
                   }
               });
           }
       });
   }

   @Override
   public int getItemCount() {
       return eventModelArrayList.size();
   }

    public class ViewHolder extends RecyclerView.ViewHolder {
       CardView event_card;
       TextView date, month, title, place, time;
       ImageView menu;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           event_card = itemView.findViewById(R.id.eventCardView);
           date = itemView.findViewById(R.id.day);
           month = itemView.findViewById(R.id.month);
           title = itemView.findViewById(R.id.eventTitle);
           place = itemView.findViewById(R.id.location);
           time = itemView.findViewById(R.id.time);
           menu = itemView.findViewById(R.id.menu);
       }
    }

    // Get month name from list
    public String getMonthName(int monthNo){
        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("JAN");
        monthList.add("FEB");
        monthList.add("MAR");
        monthList.add("APR");
        monthList.add("MAY");
        monthList.add("JUNE");
        monthList.add("JULY");
        monthList.add("AUG");
        monthList.add("SEP");
        monthList.add("OCT");
        monthList.add("NOV");
        monthList.add("DEC");
        return monthList.get(monthNo - 1);
    }

    // Obtain event ID from Google Calendar
    private int ListSelectedCalendars(String eventtitle) {
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way
            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way
            eventUri = Uri.parse("content://com.android.calendar/events");
        }
        int result = 0;
        String projection[] = { "_id", "title" };
        Cursor cursor = context.getContentResolver().query(eventUri, null, null, null,
                null);
        if (cursor.moveToFirst()) {
            String calName;
            String calID;

            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);

                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    // Delete event from Google Calendar
    private int DeleteCalendarEntry(int entryID) {
        int iNumRowsDeleted = 0;
        Uri eventUri = ContentUris
                .withAppendedId(getCalendarUriBase(), entryID);
        iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
        return iNumRowsDeleted;
    }

    private Uri getCalendarUriBase() {
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way
            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way
            eventUri = Uri.parse("content://com.android.calendar/events");
        }
        return eventUri;
    }
}

