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
import android.text.TextUtils;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        // Obtain event ID from selected event in order to load event details
        int eventID = fromEventPage.getIntExtra("EventDetails", -1);
        // Check if user is trying to edit the event
        editable = fromEventPage.getBooleanExtra("Editable", false);
        // Check if user is trying to create a new event
        newEvent = fromEventPage.getBooleanExtra("NewEvent", false);
        // Initialise views
        initDetails();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If user is editing or viewing an event, initialise selected event's details
        if (!newEvent){
            selectedEvent = Event.getEventForID(eventID);
            eventName.setText(selectedEvent.getName());
            eventLocation.setText(selectedEvent.getLocation());
            eventDate.setText(selectedEvent.getDate().toString());
            eventTime.setText(selectedEvent.getTime());
            eventDesc.setText(selectedEvent.getDesc());
            currentName = eventName.getText().toString();
        }
        else{
            // Set default date displayed to be current date
            // Format month number to show two digits
            DecimalFormat formatter = new DecimalFormat("00");
            String monthString = formatter.format(month + 1);
            String sDate = year + "-" + monthString + "-" + day;
            eventDate.setText(sDate);

        }
        // If user is viewing an event, button will be invisible
        if (!editable && !newEvent){
            greenBtn.setVisibility(View.INVISIBLE);
        }

        // If user is editing or creating an event
        if (editable || newEvent){
            // Display selected date using calendar
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
            // Display selected time
            selectTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                            hour = selectedHour;
                            min = selectedMin;
                            // Convert 24h to 12h format
                            checkAmOrPm(hour, min);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), onTimeSetListener, hour, min, true);
                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
            });
            // Allow inputs to be focusable (editable)
            setFocusable();
            //
            if (editable){
                greenBtn.setText("Save Changes");
                // When button is clicked, save changes to event
                greenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Validation for event name and location
                        if (TextUtils.isEmpty(eventName.getText().toString())){
                            eventName.setError("Enter an Event Name");
                            return;
                        }
                        if (TextUtils.isEmpty(eventLocation.getText().toString())){
                            eventLocation.setError("Enter a location");
                            return;
                        }
                        if (TextUtils.isEmpty(eventDesc.getText().toString())){
                            eventDesc.setError("Enter description");
                        }
                        // Save edited event details into firebase
                        EventEditActivity.removeDataFromFireBase(userId, eventID);
                        EventEditActivity.addDataToFireBase(userId, eventID, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString(), eventDesc.getText().toString());
                        // Convert date string to LocalDate to store inside Event object
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String d = eventDate.getText().toString();
                        LocalDate date;
                        try{
                            date = LocalDate.parse(d, dtf);
                        }catch (DateTimeParseException e){
                            DateTimeFormatter dtfv = DateTimeFormatter.ofPattern("yyyy-MM-d");
                            date = LocalDate.parse(d, dtfv);
                        }
                        // Check that selected date has not past
                        if (date.isBefore(LocalDate.now())){
                            eventDate.setError("Select a valid date");
                            return;
                        }
                        Event editedEvent = new Event(eventID, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString(), eventDesc.getText().toString());
                        // Replace previous event with edited event
                        for (Event e : Event.eventsList){
                            if (e.getID() == eventID){
                                Event.eventsList.set(Event.eventsList.indexOf(e), editedEvent);
                            }
                        }
                        // Update event details to Google Calendar
                        try{UpdateCalendarEntry(ListSelectedCalendars(currentName));}catch (Exception e){Toast.makeText(EventDetails.this, "Allow permissions to sync with Google Calendar", Toast.LENGTH_LONG).show();}
                        Toast.makeText(EventDetails.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            // If user is creating a new event
            else if (newEvent){
                greenBtn.setText("Create Event");
                // When button is clicked, save the newly created event
                greenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Validation for event name and location
                        if (TextUtils.isEmpty(eventName.getText().toString())){
                            eventName.setError("Enter an Event Name");
                            return;
                        }
                        if (TextUtils.isEmpty(eventLocation.getText().toString())){
                            eventLocation.setError("Enter a location");
                            return;
                        }
                        if (TextUtils.isEmpty(eventDesc.getText().toString())){
                            eventDesc.setError("Enter description");
                            return;
                        }
                        // Give new event an ID
                        int id = 1;
                        if (Event.eventsList.size() != 0){
                            Event previousEvent = Event.eventsList.get(Event.eventsList.size() - 1);
                            id = previousEvent.getID() + 1;
                        }

                        // Format date string to LocalDate to store inside Event object
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String d = eventDate.getText().toString();
                        LocalDate date;
                        try{
                            date = LocalDate.parse(d, dtf);
                        }catch (DateTimeParseException e){
                            DateTimeFormatter dtfv = DateTimeFormatter.ofPattern("yyyy-MM-d");
                            date = LocalDate.parse(d, dtfv);
                        }
                        // Check that selected date has not past
                        if (date.isBefore(LocalDate.now())){
                            eventDate.setError("Select a valid date");
                            return;
                        }
                        Event event = new Event(id, eventName.getText().toString(), eventLocation.getText().toString(), date, eventTime.getText().toString(), eventDesc.getText().toString());
                        // Add newly created event to eventList
                        Event.eventsList.add(event);
                        // Add newly created event to Firebase
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        userId = user.getUid();
                        EventEditActivity.addDataToFireBase(userId, id, eventName.getText().toString(), eventLocation.getText().toString(), eventTime.getText().toString(), eventDate.getText().toString(), eventDesc.getText().toString());

                        ContentResolver cr = EventDetails.this.getContentResolver();
                        ContentValues cv = new ContentValues();
                        // Add newly created event to Google Calendar
                        try {
                            cv.put(CalendarContract.Events.CALENDAR_ID, eventID);
                            cv.put(CalendarContract.Events.TITLE, eventName.getText().toString());
                            cv.put(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString());
                            cv.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());
                        }
                        catch (Exception e){}

                        // Convert time from 24h to 12h format
                        String time;
                        try{
                            time = LocalTime.parse(eventTime.getText().toString(),
                                    DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));
                        }catch (DateTimeParseException e){
                            time = LocalTime.parse(eventTime.getText().toString(),
                                    DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));
                        }

                        // Split time string to get hour and minute
                        String[] arrOfTime = time.split(":");
                        int hour = Integer.parseInt(arrOfTime[0]);
                        int min = Integer.parseInt(arrOfTime[1]);

                        // Set date and time for google calendar
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = sdf.parse(eventDate.getText().toString());
                            cal.setTime(date1);
                            cal.set(Calendar.HOUR_OF_DAY, hour);
                            cal.set(Calendar.MINUTE, min);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try{
                            // Initialise event details into Google Calendar
                            cv.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
                            cv.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
                            cv.put(CalendarContract.Events.CALENDAR_ID, 1);
                            cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);}
                        // Display toast message if user did not allow permissions to access calendar for the app (Events will be unable to sync with Google Calendar)
                        catch(Exception e){Toast.makeText(EventDetails.this, "Allow permissions to sync with Google Calendar", Toast.LENGTH_LONG).show();}
                        Toast.makeText(EventDetails.this, "New Event Created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }
        // When closeBtn is clicked, close activity
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Initialises views
    private void initDetails(){
        greenBtn = findViewById(R.id.greenBtn);
        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        selectTime = findViewById(R.id.selectTime);
        selectDate= findViewById(R.id.selectDate);
        eventDesc = findViewById(R.id.eventDesc);
    }

    // Make inputs focusable (editable)
    private void setFocusable(){
        eventName.setFocusableInTouchMode(true);
        eventLocation.setFocusableInTouchMode(true);
        selectDate.setFocusableInTouchMode(true);
        selectTime.setFocusableInTouchMode(true);
        eventDesc.setFocusableInTouchMode(true);
    }

    // Setting time button to 12 hour format
    private void checkAmOrPm(int hour, int min){
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

    // Obtain event ID from Google Calendar
    private int ListSelectedCalendars(String eventtitle) {
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // For older version
            eventUri = Uri.parse("content://calendar/events");
        } else {
            // For newer version
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

    // Update event to Google Calendar
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

        String t;
        // Convert time from 24h to 12h format
        try{
            t = LocalTime.parse(eventTime.getText().toString(),
                    DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
        }catch (DateTimeParseException e){
            t = LocalTime.parse(eventTime.getText().toString(),
                    DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        // Split time string to get hour and minute
        String[] arrOfTime = t.split(":");
        int hour = Integer.parseInt(arrOfTime[0]);
        int min = Integer.parseInt(arrOfTime[1]);

        // Set date and time for google calendar
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(eventDate.getText().toString());
            cal.setTime(date1);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        // Insert updated values into Google Calendar
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, eventName.getText().toString());
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, eventDesc.getText().toString());
        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());

        Uri updateUri = ContentUris.withAppendedId(eventUri, entryID);
        iNumRowsUpdated = this.getContentResolver().update(updateUri, values, null,
                null);

        return iNumRowsUpdated;
    }


}