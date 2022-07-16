package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class EventDetails extends AppCompatActivity {
    private Event selectedEvent;
    private ImageView closeBtn;
    private int hour, min;
    private TextView eventName, eventLocation, eventDate, eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent fromEventPage = getIntent();
        int eventID = fromEventPage.getIntExtra("EventDetails", -1);
        Log.e("Event ID", String.valueOf(eventID));
        selectedEvent = Event.getEventForID(eventID);
        Log.e("Event Name", selectedEvent.getName());
        eventName = findViewById(R.id.eventName);
        eventName.setText(selectedEvent.getName());
        eventLocation = findViewById(R.id.eventLocation);
        eventLocation.setText(selectedEvent.getLocation());
        eventDate = findViewById(R.id.eventDate);
        eventDate.setText(selectedEvent.getDate().toString());
        eventTime = findViewById(R.id.eventTime);
        eventTime.setText(selectedEvent.getTime());

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void selectTime(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                hour = selectedHour;
                min = selectedMin;
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, min, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}