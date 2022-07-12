package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Month;
import java.util.ArrayList;


public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
   private ArrayList<Event> eventModelArrayList = new ArrayList<>();

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
       String date = Event.eventsList.get(position).getDate().toString();
       String[] dateArr = date.split("-", 3);
       int monthNo = Integer.parseInt(dateArr[1]);
       Month monthName = Month.of(monthNo);
       holder.date.setText(dateArr[2]);
       //holder.date.setText(eventModelArrayList.get(position).getDay());
       holder.month.setText(monthName.toString());
       holder.title.setText(Event.eventsList.get(position).getName());
       holder.place.setText(Event.eventsList.get(position).getLocation());
       int id = Event.eventsList.get(position).getID();

       /*
       holder.itemView.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               Intent createEvent = new Intent(view.getContext(), EventDetails.class);
               createEvent.putExtra("EventDetails", id);
               view.getContext().startActivity(createEvent);
           }
       });

        */
   }

   @Override
   public int getItemCount() {
       return eventModelArrayList.size();
   }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView date, month, title, place;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           date = itemView.findViewById(R.id.day);
           month = itemView.findViewById(R.id.month);
           title = itemView.findViewById(R.id.eventTitle);
           place = itemView.findViewById(R.id.location);
       }
    }
}

