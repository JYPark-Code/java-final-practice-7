import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {

    private final LocalDate date;
    private final LocalTime lessonStart;
    private final LocalTime arrivalTime;
    private final AttendanceStatus status;

    public Attendance(LocalDate date, LocalTime lessonStart,
                      LocalTime arrivalTime, AttendanceStatus status) {
        this.date = date;
        this.lessonStart = lessonStart;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getLessonStart() {
        return lessonStart;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }
}
