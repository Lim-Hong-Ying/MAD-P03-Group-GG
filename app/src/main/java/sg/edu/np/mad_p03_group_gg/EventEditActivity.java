package sg.edu.np.mad_p03_group_gg;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;

// NOTE: EventEditActivity no longer in use for stage 2
public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET, locationNameET;
    private TextView eventDateTV;
    private Button timeBtn;
    private int hour, min;
    private String time;
    // Get selected event
    private Event selectedEvent;
    // Get userId of current user
    private String userId = HomepageFragment.userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        // Get current userId from homepage
        userId = HomepageFragment.userId;
        // Show selected date
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        Intent previousIntent = getIntent();
        // Get eventId of selected event if intent is called
        int passedEventID = previousIntent.getIntExtra("eventEdit", -1);
        // Get selected event
        selectedEvent = Event.getEventForID(passedEventID);
        // Initialise event details for editing / deleting events
        if (selectedEvent != null){ // Selected event not null means deleting/editing events
            // Initialise event details when previously created event is clicked
            eventNameET.setText(selectedEvent.getName());
            locationNameET.setText(selectedEvent.getLocation());
            timeBtn = findViewById(R.id.timeBtn);
            timeBtn.setText(selectedEvent.getTime());
        }

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Initialise event details
    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        locationNameET = findViewById(R.id.locationNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        timeBtn = findViewById(R.id.timeBtn);
    }

    // Create the interface for selecting time
    public void timePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                hour = selectedHour;
                min = selectedMin;
                // Display time in 12 hour format on time button
                checkAmOrPm(hour, min);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, min, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    // Save or edit event into Firebase when save button is clicked
    public void saveEventAction(View view)
    {
        // Format event details into strings to store into Firebase
        String eventName = eventNameET.getText().toString();
        String location = locationNameET.getText().toString();
        // Change time to 12 hour format before storing into firebase
        String am_Pm = "AM";
        String sHour = String.valueOf(hour);
        String sMin = String.valueOf(min);
        // If time is after 12:00 it is PM
        if (hour >= 12){
            if (hour != 12){
                hour -= 12;
            }
            am_Pm = "PM";
        }
        // If time is before 12:00 it is AM
        else{
            // 00:00 = 12:00 AM
            if (hour == 0){
                hour = 12;
            }
        }
        // Converting time to string
        if (hour >= 10){
            time = sHour.format("%02d", hour) + ":" + sMin.format("%02d", min) + " " + am_Pm;
        }
        else {
            time = sHour.format("%01d", hour) + ":" + sMin.format("%02d", min) + " " + am_Pm;
        }

        // Get eventId for events
        int eventId;
        // Set eventId to 1 if it is the first event created by the user
        if (Event.eventsList.size() == 0){
            eventId = 1;
        }
        // Find previous event to obtain the next eventId
        else{
            Event previousEvent = Event.eventsList.get(Event.eventsList.size() - 1);
            eventId = previousEvent.getID() + 1;
        }

        // Validation for event name and location
        if (TextUtils.isEmpty(eventName)){
            eventNameET.setError("Enter an Event Name");
            return;
        }
        if (TextUtils.isEmpty(location)){
            locationNameET.setError("Enter a location");
            return;
        }

        // Add new event into Firebase
        if (selectedEvent == null){
            // Initisalise new event
            Event newEvent = new Event(eventId, eventName, location, CalendarUtils.selectedDate, time, null);
            // Add to list of events
            Event.eventsList.add(newEvent);
            // Add to firebase
            addDataToFireBase(userId, eventId, eventName, location, time, CalendarUtils.selectedDate.toString(), null);
        }
        // Edit event details and saving it into Firebase
        else{
            // Remove event from firebase before adding the edited version
            removeDataFromFireBase(userId, selectedEvent.getID());
            // Initialising the edited event details
            selectedEvent.setName(eventName);
            selectedEvent.setLocation(location);
            selectedEvent.setTime(time);
            addDataToFireBase(userId, selectedEvent.getID(), eventName, location, time, CalendarUtils.selectedDate.toString(), null);
        }
        /*
        // Intent to Google Calendar
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, eventName);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        startActivity(intent);

         */

        finish();
    }

    // Delete event when delete button is clicked
    public void deleteEventAction(View view){
        try {
            removeDataFromFireBase(userId, selectedEvent.getID());
            Event.eventsList.remove(selectedEvent);
        }
        // Shows toast message if user tries to delete event before creating one
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "Unable to delete event", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // Setting time button to 12 hour format
    public void checkAmOrPm(int hour, int min){
        String am_Pm;
        if (hour > 11) {
            am_Pm = "PM";
            if (hour != 12) {
                hour -= 12;
            }
        }
        else{
            am_Pm = "AM";
            if (hour == 0){
                hour = 12;
            }
        }
        Log.d("Hour", String.valueOf(hour));
        if (hour >= 10){
            timeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min) + " " + am_Pm);
        }
        else{
            timeBtn.setText(String.format(Locale.getDefault(), "%01d:%02d", hour, min) + " " + am_Pm);
        }
    }

    // Adding event details into Firebase
    public static void addDataToFireBase(String userID, int id, String name, String location, String time, String date, String desc){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Planner");
        DatabaseReference user = myRef.child(userID);
        DatabaseReference eventId = user.child("" + id);
        DatabaseReference userName = eventId.child("name");
        userName.setValue(name);
        DatabaseReference userLocation = eventId.child("location");
        userLocation.setValue(location);
        DatabaseReference userTime = eventId.child("time");
        userTime.setValue(time);
        DatabaseReference userDate = eventId.child("date");
        userDate.setValue(date);
        DatabaseReference userDesc = eventId.child("description");
        userDesc.setValue(desc);
    }

    // Deleting event from Firebase
    public static void removeDataFromFireBase(String userId, int eventId){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Planner").child(userId);
        DatabaseReference event = myRef.child(String.valueOf(eventId));
        event.removeValue();
    }

}