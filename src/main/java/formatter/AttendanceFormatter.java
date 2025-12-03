package formatter;

import domain.Attendance;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttendanceFormatter {

    private static final DateTimeFormatter dayFormatter
            = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN);

    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    public static String format(Attendance attendance) {
        String dayText = attendance.getDate().format(dayFormatter);
        String timeText = formatTime(attendance);
        String statusText = attendance.getStatus().getText();
        return dayText + " " + timeText + " (" + statusText + ")";

    }

    public static String formatTimeAndStatus(Attendance attendance){
        String timeText = formatTime(attendance);
        String statusText = attendance.getStatus().getText();
        return timeText + " (" + statusText + ")";
    }

    private static String formatTime(Attendance attendance) {
        if (attendance.getArrivalTime() == null) {
            return "--:--";
        }
        return attendance.getArrivalTime().format(timeFormatter);
    }


}
