package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.util.Log;
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

import java.time.Month;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;


public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
   private ArrayList<Event> eventModelArrayList = new ArrayList<>();
   private ArrayList<String> monthList = new ArrayList<>();
    private String userId = HomepageFragment.userId;

   public EventsRecyclerViewAdapter(ArrayList<Event> eventModelArrayList) {
       this.eventModelArrayList = eventModelArrayList;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
       return new ViewHolder(cardView);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Event e = Event.eventsList.get(position);
       String date = Event.eventsList.get(position).getDate().toString();
       int id = Event.eventsList.get(position).getID();
       String[] dateArr = date.split("-", 3);
       int monthNo = Integer.parseInt(dateArr[1]);
       //Month monthName = Month.of(monthNo);
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
       holder.date.setText(dateArr[2]);
       //holder.date.setText(eventModelArrayList.get(position).getDay());
       holder.month.setText(monthList.get(monthNo));
       holder.title.setText(Event.eventsList.get(position).getName());
       holder.place.setText(Event.eventsList.get(position).getLocation());
       holder.event_card.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               Intent createEvent = new Intent(view.getContext(), EventDetails.class);
               createEvent.putExtra("EventDetails", id);
               view.getContext().startActivity(createEvent);
           }
       });
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
                           case R.id.edit:
                               Intent editEvent = new Intent(view.getContext(), EventDetails.class);
                               editEvent.putExtra("EventDetails", id);
                               editEvent.putExtra("Editable", true);
                               view.getContext().startActivity(editEvent);
                               return true;
                           case R.id.delete:
                               Event.eventsList.remove(e);
                               EventEditActivity.removeDataFromFireBase(userId, id);
                               notifyDataSetChanged();
                               EventsPage.noOfEvent(Event.eventsList);
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
       TextView date, month, title, place;
       ImageView menu;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           event_card = itemView.findViewById(R.id.eventCardView);
           date = itemView.findViewById(R.id.day);
           month = itemView.findViewById(R.id.month);
           title = itemView.findViewById(R.id.eventTitle);
           place = itemView.findViewById(R.id.location);
           menu = itemView.findViewById(R.id.menu);
       }
    }
}

