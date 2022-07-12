package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventsPage extends AppCompatActivity {
    RecyclerView eventRV;
    EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private TextView eventTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_page);
        eventTV = findViewById(R.id.eventTV);
        eventRV = findViewById(R.id.eventRecyclerView);
        initRecyclerView();
        //filterEvent(Event.eventsList);
        noOfEvent(Event.eventsList);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initRecyclerView();
        noOfEvent(Event.eventsList);
    }

    public void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(Event.eventsList);
        eventRV.setLayoutManager(linearLayoutManager);
        eventRV.setAdapter(eventsRecyclerViewAdapter);
        eventsRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void noOfEvent(ArrayList<Event> eventsList){
        String eventNumberText;
        int count = eventsList.size();
        if (count == 0){
            eventNumberText = "No Event planned currently";
        }
        else{
            eventNumberText = count + " Events planned by you";
        }
        eventTV.setText(eventNumberText);
    }

    public void createEvent(View view){
        Intent createEvent = new Intent(EventsPage.this, EventDetails.class);
        // Pass eventID to EventDetails activity
        //Event selectedEvent = (Event) eventRV.getItemAtPosition(position);
        //createEvent.putExtra("eventEdit", selectedEvent.getID());
        startActivity(createEvent);
    }

    /*
    public void filterEvent(ArrayList<Event> eventsList){
        for(Event event : eventsList){
            Log.e("Event Date", event.getDate().toString());
            Log.e("Today", LocalDateTime.now().toString());
            if (event.getDate().equals(LocalDateTime.now())){
                continue;
            }
            else{
                eventsList.remove(event);
            }
        }
    }

     */
}