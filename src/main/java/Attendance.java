import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {

    private final LocalDate date;
    private final LocalTime lessonStart;
    private final LocalTime arrivalTime;
    private final AttendanceStatus status;

    public Attendance(LocalDate date, LocalTime lessonStart,
                      LocalTime arrivalTime, AttendanceStatus status) {

        if (date == null || lessonStart == null || arrivalTime == null || status == null) {
            throw new IllegalArgumentException("출석 시간은 필수 입력값입니다.");
        }

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
