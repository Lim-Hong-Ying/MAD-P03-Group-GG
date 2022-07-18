package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class EventsPage extends AppCompatActivity {
    RecyclerView eventRV;
    EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private static TextView eventTV;
    private EditText searchEvent;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_page);

        //objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        eventTV = findViewById(R.id.eventTV);
        eventRV = findViewById(R.id.eventRecyclerView);
        initRecyclerView();
        //filterEvent(Event.eventsList);
        noOfEvent(Event.eventsList);
        searchEvent = findViewById(R.id.searchEvent);
        searchEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventsPage.this, EventEditActivity.class);
                startActivity(i);
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initRecyclerView();
        noOfEvent(Event.eventsList);
        // Requries API 24 (to fix this issue soon)
        Event.eventsList.sort(Comparator.comparing(o -> o.getDate()));
    }

    public void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(Event.eventsList);
        eventRV.setLayoutManager(linearLayoutManager);
        eventRV.setAdapter(eventsRecyclerViewAdapter);
        eventsRecyclerViewAdapter.notifyDataSetChanged();
    }

    public static void noOfEvent(ArrayList<Event> eventsList){
        String eventNumberText;
        int count = eventsList.size();
        if (count == 0){
            eventNumberText = "No Event planned currently";
        }

        else if (count == 1){
            eventNumberText = "1 Event planned by you";
        }
        else{
            eventNumberText = count + " Events planned by you";
        }
        eventTV.setText(eventNumberText);
    }

    public void editEvent(View view){
        Intent i = new Intent(EventsPage.this, WeekViewActivity.class);
        startActivity(i);
    }

    public void createEvent(View view){
        Intent newEvent = new Intent(EventsPage.this, EventDetails.class);
        newEvent.putExtra("NewEvent", true);
        startActivity(newEvent);
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