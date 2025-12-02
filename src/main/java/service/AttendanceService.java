package service;

import entity.Attendance;
import entity.Student;
import repository.StudentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


// Command Refactoring
public class AttendanceService {

    private final StudentRepository repository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public AttendanceService(StudentRepository repository) {
        this.repository = repository;
    }

    // 1번 : 출석
    public Attendance recordAttendance(String name, String timeText){
        LocalDate today = LocalDate.now();
        validateWeekday(today);

        Student student = findStudent(name);
        ensureNotAlreadyChecked(student, today);

        LocalTime lessonStart = getLessonStart(today);
        LocalTime arrivalTime = parseTime(timeText);

        Attendance attendance = Attendance.fromArrival(today, lessonStart, arrivalTime);
        student.addAttendance(attendance);
        return attendance;
    }

    // 2번 : 출석 수정
    public AttendanceEditResult editAttendance(String name, int day, String timeText){
        Student student = findStudent(name);

        LocalDate today = LocalDate.now();
        LocalDate targetDate = LocalDate.of(today.getYear(), today.getMonth(), day);
        LocalTime lessonStart = getLessonStart(targetDate);

        if(!student.hasAttendance(targetDate)){
            throw new IllegalArgumentException("출석하지 않았습니다. 출석 기능을 이용해주세요.");
        }

        LocalTime arrivalTime = parseTime(timeText);
        Attendance newAttendance = Attendance.fromArrival(targetDate, lessonStart, arrivalTime);
        Attendance oldAttendance = student.editAttendance(targetDate, newAttendance);

        return new AttendanceEditResult(oldAttendance, newAttendance);

    }

    // 주말 여부 확인
    private void validateWeekday(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("[ERROR] 주말에는 출석을 받지 않습니다.");
        }
    }

    // 등록된 학생 여부 확인
    private Student findStudent(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));
    }

    //  오늘 출석했는지 아닌지 확인
    private void ensureNotAlreadyChecked(Student student, LocalDate date) {
        if (student.hasAttendance(date)) {
            throw new IllegalArgumentException("이미 출석 확인하였습니다. 필요한 경우 수정 기능을 이용해주세요.");
        }
    }

    // 시간 HH:mm로 파싱하기
    private LocalTime parseTime(String timeText){
        try {
            return LocalTime.parse(timeText, timeFormatter); // HH:mm
        } catch (Exception e) {
            throw new IllegalArgumentException("시간 형식이 올바르지 않습니다. 예: 09:00, 13:30");
        }
    }

    // 오늘 출석 시간 체크
    private LocalTime getLessonStart(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.MONDAY) {
            return LocalTime.of(13, 0);
        }
        return LocalTime.of(10, 0);
    }

    // 출석 수정 결과 DTO (기존/변경 후 시간 표기)
    public static class AttendanceEditResult {
        private final Attendance oldAttendance;
        private final Attendance newAttendance;

        public AttendanceEditResult(Attendance oldAttendance, Attendance newAttendance) {
            this.oldAttendance = oldAttendance;
            this.newAttendance = newAttendance;
        }

        public Attendance getOldAttendance() {
            return oldAttendance;
        }

        public Attendance getNewAttendance() {
            return newAttendance;
        }
    }

}
