package sg.edu.np.mad_p03_group_gg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad_p03_group_gg.view.ui.fragments.HomepageFragment;

public class EventDetails extends AppCompatActivity {
    private Event selectedEvent;
    private ImageView closeBtn, selectDate, selectTime;
    private int hour, min;
    private boolean editable, newEvent;
    private TextView eventName, eventLocation, eventDate, eventTime, eventDesc;
    private MaterialButton greenBtn;
    private String userId = HomepageFragment.userId, sMonth, mth, currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent fromEventPage = getIntent();
        int eventID = fromEventPage.getIntExtra("EventDetails", -1);
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
            eventDesc.setText(selectedEvent.getDesc());
            currentName = eventName.getText().toString();
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
            eventDesc.setFocusableInTouchMode(true);
            greenBtn.setText("Save Edit");
            greenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventEditActivity.removeDataFromFireBase(userId, eventID);
                    EventEditActivity.addDataToFireBase(userId, eventID, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString(), eventDesc.getText().toString());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String d = eventDate.getText().toString();
                    LocalDate date = LocalDate.parse(d,dtf);
                    Event editedEvent = new Event(eventID, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString(), eventDesc.getText().toString());
                    for (Event e : Event.eventsList){
                        if (e.getID() == eventID){
                            Event.eventsList.set(Event.eventsList.indexOf(e), editedEvent);
                        }
                    }
                    UpdateCalendarEntry(ListSelectedCalendars(currentName));
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
            eventDesc.setFocusableInTouchMode(true);
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
                    Event event = new Event(id, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString(), eventDesc.getText().toString());
                    Event.eventsList.add(event);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userId = user.getUid();
                    EventEditActivity.addDataToFireBase(userId, id, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString(), eventDesc.getText().toString());


                    ContentResolver cr = EventDetails.this.getContentResolver();
                    ContentValues cv = new ContentValues();
                    cv.put(CalendarContract.Events.CALENDAR_ID, eventID);
                    cv.put(CalendarContract.Events.TITLE, eventName.getText().toString());
                    cv.put(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString());
                    cv.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());

                    String time = LocalTime.parse(eventTime.getText().toString(),
                            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                    String[] arrOfTime = time.split(":");
                    int hour = Integer.parseInt(arrOfTime[0]);
                    int min = Integer.parseInt(arrOfTime[1]);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = sdf.parse(eventDate.getText().toString());
                        cal.setTime(date1);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, min);
                        Log.e("Year", cal.getTime().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    cv.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
                    cv.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
                    //cv.put(CalendarContract.Events.DTSTART, eventDate.getText().toString());
                    cv.put(CalendarContract.Events.CALENDAR_ID, 1);
                    cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);

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
        Cursor cursor = this.getContentResolver().query(eventUri, null, null, null,
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

    private int UpdateCalendarEntry(int entryID) {
        int iNumRowsUpdated = 0;

        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, eventName.getText().toString());
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString());

        Uri updateUri = ContentUris.withAppendedId(eventUri, entryID);
        iNumRowsUpdated = this.getContentResolver().update(updateUri, values, null,
                null);

        return iNumRowsUpdated;
    }


}