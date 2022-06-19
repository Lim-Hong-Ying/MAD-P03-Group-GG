package sg.edu.np.mad_p03_group_gg;

import static sg.edu.np.mad_p03_group_gg.CalendarUtils.daysInMonthArray;
import static sg.edu.np.mad_p03_group_gg.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class MonthViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);
        // Initialise date only for the first time
        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = LocalDate.now();
        }
        initWidgets();
        setMonthView();
        // Initialise close button
        ImageView closeBtn = findViewById(R.id.weeklyCloseButton);
        // Removes activity from stack
        closeBtn.setOnClickListener(view -> {
            // Pass selected date to weekly calendar
            WeekViewActivity.monthlyDate =  CalendarUtils.selectedDate;
            finish();
        });
    }

    // initialise recycler view and text
    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    // create monthly calendar view
    private void setMonthView()
    {
        // Set month and year of monthly calendar
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        // Get days in month
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    // Navigate to previous month of selected date
    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    // Navigate to next month of selected date
    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    // Direct to weekly calendar
    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }
}