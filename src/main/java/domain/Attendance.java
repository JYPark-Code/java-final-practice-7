package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

// 값 객체 VO
public class Attendance {

    private final LocalDate date;
    private final LocalTime lessonStart;
    private final LocalTime arrivalTime;
    private final AttendanceStatus status;

    // 외부에서는 이 생성자를 직접 쓰지 않게 막고,
    // 항상 아래의 정적 팩토리 메서드를 통해 생성하게 만든다.
    private Attendance(LocalDate date,
                       LocalTime lessonStart,
                       LocalTime arrivalTime,
                       AttendanceStatus status) {
        this.date = Objects.requireNonNull(date, "date는 null이면 안 됩니다.");
        this.lessonStart = Objects.requireNonNull(lessonStart, "lessonStart는 null이면 안 됩니다.");
        // arrivalTime은 null 허용 여부를 정책으로 정하면 됨.
        this.arrivalTime = arrivalTime;
        this.status = Objects.requireNonNull(status, "status는 null이면 안 됩니다.");
    }


    /**
     * 정상적으로 등교 시간을 입력한 경우에 사용하는 팩토리 메서드.
     * 예: 학생이 등교 체크를 했을 때
     */
    public static Attendance fromArrival(LocalDate date,
                                         LocalTime lessonStart,
                                         LocalTime arrivalTime) {
        Objects.requireNonNull(arrivalTime, "등교 시간(arrivalTime)은 null이면 안 됩니다.");

        AttendanceStatus status = decideStatus(lessonStart, arrivalTime);
        return new Attendance(date, lessonStart, arrivalTime, status);
    }

    /**
     * 등교 체크를 아예 하지 않고 하루가 지난 무단 결석 같은 경우
     *
     */
    public static Attendance absentWithoutCheck(LocalDate date,
                                                LocalTime lessonStart) {
        return new Attendance(date, lessonStart, null, AttendanceStatus.ABSENT);
    }

    /**
     * 수업 시작 시간과 실제 도착 시간으로 출석/지각/결석을 판단하는 도메인 규칙.
     * 규칙 예시 (네 설명 기반으로 맞춰놓은 것):
     *
     * - 수업 시작 ~ 5분 이내: 출석
     * - 5분 초과 ~ 30분 미만: 지각
     * - 30분 이상: 결석
     *
     * 필요하면 숫자들(5, 30)만 바꿔도 된다.
     */
    private static AttendanceStatus decideStatus(LocalTime lessonStart,
                                                 LocalTime arrivalTime) {
        long diffMinutes = ChronoUnit.MINUTES.between(lessonStart, arrivalTime);
        // arrivalTime - lessonStart 라고 생각하면 됨

        if (diffMinutes <= 5) {
            return AttendanceStatus.PRESENT; // or LATE로 보고 싶으면 여기만 바꾸면 됨
        } else if (diffMinutes < 30) {
            return AttendanceStatus.LATE;
        } else {
            return AttendanceStatus.ABSENT;
        }
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

    public boolean isPresent(){
        return status == AttendanceStatus.PRESENT;
    }

    public boolean isLate(){
        return status == AttendanceStatus.LATE;
    }

    public boolean isAbsent(){
        return status == AttendanceStatus.ABSENT;
    }
}
