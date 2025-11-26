import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttendanceFormatter {
    public static String format(Attendance attendance) {

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String dayText = attendance.getDate().format(dayFormatter);

        String timeText;
        if (attendance.getArrivalTime() == null){
            timeText = "--:--";
        } else {
            timeText = attendance.getArrivalTime().format(timeFormatter);
        }

        String statusText = switch (attendance.getStatus()) {
            case PRESENT -> "출석";
            case LATE -> "지각";
            case ABSENT -> "결석";
        };

        return dayText + " " + timeText + " (" + statusText + ")";

    }
}
