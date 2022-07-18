package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;

public class EventDetails extends AppCompatActivity {
    private Event selectedEvent;
    private ImageView closeBtn, selectDate, selectTime;
    private int hour, min;
    private boolean editable, newEvent;
    private TextView eventName, eventLocation, eventDate, eventTime;
    private EditText eventDesc;
    private MaterialButton greenBtn;
    private String userId = HomepageFragment.userId, sMonth, mth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent fromEventPage = getIntent();
        int eventID = fromEventPage.getIntExtra("EventDetails", -1);
        Log.e("Event ID", String.valueOf(eventID));
        editable = fromEventPage.getBooleanExtra("Editable", false);
        newEvent = fromEventPage.getBooleanExtra("NewEvent", false);
        greenBtn = findViewById(R.id.greenBtn);
        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        selectTime = findViewById(R.id.selectTime);
        selectDate= findViewById(R.id.selectDate);
        eventDesc = findViewById(R.id.eventDesc);

        if (!newEvent){
            selectedEvent = Event.getEventForID(eventID);
            eventName.setText(selectedEvent.getName());
            eventLocation.setText(selectedEvent.getLocation());
            eventDate.setText(selectedEvent.getDate().toString());
            eventTime.setText(selectedEvent.getTime());
            //eventDesc.setText(selectedEvent.getDesc());
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If user is editing the page
        if (editable){
            selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dialog = new DatePickerDialog(EventDetails.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month += 1;
                            mth = Integer.toString(month);
                            if (month < 10){
                                sMonth = "0" + mth;
                            }
                            else{
                                sMonth = mth;
                            }
                            String date = year + "-" + sMonth + "-" + day;
                            eventDate.setText(date);
                        }
                    }, year, month, day);
                    dialog.show();
                }
            });
            selectTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                            hour = selectedHour;
                            min = selectedMin;
                            checkAmOrPm(hour, min);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), onTimeSetListener, hour, min, true);
                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
            });

            eventName.setFocusableInTouchMode(true);
            eventLocation.setFocusableInTouchMode(true);
            selectDate.setFocusableInTouchMode(true);
            selectTime.setFocusableInTouchMode(true);
            greenBtn.setText("Save Edit");
            greenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventEditActivity.removeDataFromFireBase(userId, eventID);
                    EventEditActivity.addDataToFireBase(userId, eventID, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String d = eventDate.getText().toString();
                    LocalDate date = LocalDate.parse(d,dtf);
                    Event editedEvent = new Event(eventID, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString());
                    for (Event e : Event.eventsList){
                        if (e.getID() == eventID){
                            Event.eventsList.set(Event.eventsList.indexOf(e), editedEvent);
                        }
                    }
                    Toast.makeText(EventDetails.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // If user is creating a new event
        else if (newEvent){
            selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dialog = new DatePickerDialog(EventDetails.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month += 1;
                            mth = Integer.toString(month);
                            if (month < 10){
                                sMonth = "0" + mth;
                            }
                            else{
                                sMonth = mth;
                            }
                            String date = year + "-" + sMonth + "-" + day;
                            eventDate.setText(date);
                        }
                    }, year, month, day);
                    dialog.show();
                }
            });
            selectTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                            hour = selectedHour;
                            min = selectedMin;
                            checkAmOrPm(hour, min);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), onTimeSetListener, hour, min, true);
                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
            });

            // Allow user to edit the input fields
            eventName.setFocusableInTouchMode(true);
            eventLocation.setFocusableInTouchMode(true);
            selectDate.setFocusableInTouchMode(true);
            selectTime.setFocusableInTouchMode(true);
            greenBtn.setText("Save Event");
            greenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = 1;
                    if (Event.eventsList.size() != 0){
                        Event previousEvent = Event.eventsList.get(Event.eventsList.size() - 1);
                        id = previousEvent.getID() + 1;
                    }

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String d = eventDate.getText().toString();
                    LocalDate date = LocalDate.parse(d,dtf);
                    Event event = new Event(id, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString());
                    Event.eventsList.add(event);
                    EventEditActivity.addDataToFireBase(userId, id, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString());
                    Toast.makeText(EventDetails.this, "New Event Created!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
            eventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, min) + " " + am_Pm);
        }
        else{
            eventTime.setText(String.format(Locale.getDefault(), "%01d:%02d", hour, min) + " " + am_Pm);
        }
    }

}