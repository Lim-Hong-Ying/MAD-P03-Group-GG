package sg.edu.np.mad_p03_group_gg;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET, locationNameET;
    private TextView eventDateTV;
    private Button timeBtn;
    private int hour, min;
    private String time;
    private sg.edu.np.mad_p03_group_gg.Event selectedEvent;
    private String userId = WeekViewActivity.userId; // change to current user

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        Intent previousIntent = getIntent();
        int passedEventID = previousIntent.getIntExtra("eventEdit", -1);
        selectedEvent = sg.edu.np.mad_p03_group_gg.Event.getEventForID(passedEventID);

        if (selectedEvent != null){
            eventNameET.setText(selectedEvent.getName());
            locationNameET.setText(selectedEvent.getLocation());
            timeBtn = findViewById(R.id.timeBtn);
            timeBtn.setText(selectedEvent.getTime());
        }
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        locationNameET = findViewById(R.id.locationNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        timeBtn = findViewById(R.id.timeBtn);
    }

    public void timePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                hour = selectedHour;
                min = selectedMin;
                timeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min)); // format to 2 decimal place
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, min, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        String location = locationNameET.getText().toString();
        String sHour = String.valueOf(hour);
        String sMin = String.valueOf(min);
        time= sHour.format("%02d", hour) + ":" + sMin.format("%02d", min);  // add 0 in front of int if starting int is a 0

        int eventId;
        if (Event.eventsList.size() == 0){
            eventId = 1;
        }
        else{
            Event previousEvent = Event.eventsList.get(Event.eventsList.size() - 1);
            eventId = previousEvent.getID() + 1;
        }

        if (TextUtils.isEmpty(eventName)){
            eventNameET.setError("Enter an Event Name");
            return;
        }
        if (TextUtils.isEmpty(location)){
            locationNameET.setError("Enter a location");
            return;
        }
        if (selectedEvent == null){
            Event newEvent = new Event(eventId, eventName, location, CalendarUtils.selectedDate, time);
            Event.eventsList.add(newEvent);
            addDataToFireBase(userId, eventId, eventName, location, time, CalendarUtils.selectedDate.toString());
        }
        else{
            removeDataFromFireBase(userId, selectedEvent.getID());
            selectedEvent.setName(eventName);
            selectedEvent.setLocation(location);
            selectedEvent.setTime(time);
            addDataToFireBase(userId, selectedEvent.getID(), eventName, location, time, CalendarUtils.selectedDate.toString());
        }
        finish();
    }

    // delete event button OnClick
    public void deleteEventAction(View view){
        removeDataFromFireBase(userId, selectedEvent.getID());
        Event.eventsList.remove(selectedEvent);
        finish();
    }

    public void addDataToFireBase(String userID, int id, String name, String location, String time, String date){
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
    }

    public void removeDataFromFireBase(String userId, int eventId){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Planner").child(userId);
        DatabaseReference event = myRef.child(String.valueOf(eventId));
        event.removeValue();
    }

}