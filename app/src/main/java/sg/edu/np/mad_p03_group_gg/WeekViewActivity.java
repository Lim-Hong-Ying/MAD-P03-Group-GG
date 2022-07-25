package sg.edu.np.mad_p03_group_gg;

import static sg.edu.np.mad_p03_group_gg.CalendarUtils.daysInWeekArray;
import static sg.edu.np.mad_p03_group_gg.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    // Selected date from monthly calendar
    public static LocalDate monthlyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        // Initialises date for first time
        if (CalendarUtils.selectedDate == null){
            CalendarUtils.selectedDate = LocalDate.now();
        }
        initWidgets();
        setWeekView();
        //setOnClickListener();
        ImageView closeBtn = findViewById(R.id.weeklyCloseButton);
        // Removes activity from stack when button is clicked
        closeBtn.setOnClickListener(view -> {
            // Pass selected date to weekly calendar before it is removed
            WeekViewActivity.monthlyDate =  CalendarUtils.selectedDate;
            finish();
        });
    }

    // Initialise views and text
    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventView);
    }

    // Create the weekly view calendar
    private void setWeekView()
    {
        // Display month and year
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        // Get days in week from CalendarUtils
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        // 7 columns in recycler view as there are 7 days in a week
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    // Navigate to previous week
    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    // Navigate to next week
    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    // Initialises date when selected
    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        // retrieve date from monthly calendar
        if (monthlyDate != null){
            CalendarUtils.selectedDate = monthlyDate;
            monthlyDate = null;
        }
        Log.d("Date", CalendarUtils.selectedDate.toString());
        super.onResume();
        setEventAdapter();
        // Set selected date from monthly calendar onto weekly calendar
        setWeekView();
    }

    private void setEventAdapter()
    {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    // Direct to EventEdit activity for creation of event
    public void newEventAction(View view)
    {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    // Direct to monthly calendar view
    public void monthlyAction(View view)
    {
        startActivity(new Intent(this, MonthViewActivity.class));
    }

    // Direct to Event edit activity when editing event
    private void setOnClickListener(){
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get selected event
                Event selectedEvent = (Event) eventListView.getItemAtPosition(position);
                Intent editEvent = new Intent(getApplicationContext(), EventEditActivity.class);
                // Pass eventID to EventEdit activity
                editEvent.putExtra("eventEdit", selectedEvent.getID());
                startActivity(editEvent);
            }
        });
    }

    public void testing(View view){
        startActivity(new Intent(this, EventsPage.class));
    }
}