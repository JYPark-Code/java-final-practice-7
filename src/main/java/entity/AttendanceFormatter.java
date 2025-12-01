package entity;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttendanceFormatter {

    private static final DateTimeFormatter dayFormatter
            = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN);

    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");



    public static String format(Attendance attendance) {

        String dayText = attendance.getDate().format(dayFormatter);

        String timeText;
        if (attendance.getArrivalTime() == null){
            timeText = "--:--";
        } else {
            timeText = attendance.getArrivalTime().format(timeFormatter);
        }

        String statusText = attendance.getStatus().getText();

        return dayText + " " + timeText + " (" + statusText + ")";

    }

    public static String formatTimeAndStatus(Attendance attendance){
        String timeText;
        if(attendance.getArrivalTime() == null){
            timeText = "--:--";
        } else {
            timeText = attendance.getArrivalTime().format(timeFormatter);
        }

        String statusText = attendance.getStatus().getText();

        return timeText + " (" + statusText + ")";
    }
}
