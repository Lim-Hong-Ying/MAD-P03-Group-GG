package sg.edu.np.mad_p03_group_gg;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils
{
    public static LocalDate selectedDate;

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    // Get number of days in each month
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date)
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        // First day of week
        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // i <= 42 as there are 42 squares in monthly calendar (7 squares in a row * 6 rows)
        for(int i = 1; i <= 42; i++)
        {
            // If its before the first or last day of month, add a blank square (only show current month)
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(null);
            else
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - dayOfWeek));
        }
        return  daysInMonthArray;
    }

    // Add weekly dates to list
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate)
    {
        ArrayList<LocalDate> days = new ArrayList<>();
        // First date should be Sunday
        LocalDate current = sundayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);
        // Add week into list
        while (current.isBefore(endDate))
        {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    // Search for Sunday as first day in weekly calendar is Sunday
    private static LocalDate sundayForDate(LocalDate current)
    {
        // Get one week before current date
        LocalDate oneWeekAgo = current.minusWeeks(1);
        // Find Sunday that is closest to current date
        while (current.isAfter(oneWeekAgo))
        {
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }
        return null;
    }
}
