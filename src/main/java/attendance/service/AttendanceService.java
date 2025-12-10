package attendance.service;

import attendance.domain.Attendance;
import attendance.domain.Student;
import attendance.repository.StudentRepository;
import camp.nextstep.edu.missionutils.DateTimes;

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
        LocalDate today = DateTimes.now().toLocalDate();
        validateWeekday();

        Student student = findStudent(name);
        LocalTime arrivalTime = parseTime(timeText);

        ensureNotAlreadyChecked(student, today);
        LocalTime lessonStart = getLessonStart(today);


        Attendance attendance = Attendance.fromArrival(today, lessonStart, arrivalTime);
        student.addAttendance(attendance);
        return attendance;
    }

    // 2번 : 출석 수정
    public AttendanceEditResult editAttendance(String name, int day, String timeText){
        Student student = findStudent(name);

        LocalDate today = DateTimes.now().toLocalDate();
        int lastDayOfMonth = today.lengthOfMonth(); // 이번달 마지막 날짜

        if (day < 1 || day > lastDayOfMonth){
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 날짜입니다.");
        }

        LocalDate targetDate = LocalDate.of(today.getYear(), today.getMonth(), day);
        LocalTime lessonStart = getLessonStart(targetDate);

        if(!student.hasAttendance(targetDate)){
            throw new IllegalArgumentException("[ERROR] 출석하지 않았습니다. 출석 기능을 이용해주세요.");
        }

        LocalTime arrivalTime = parseTime(timeText);
        Attendance newAttendance = Attendance.fromArrival(targetDate, lessonStart, arrivalTime);
        Attendance oldAttendance = student.editAttendance(targetDate, newAttendance);

        return new AttendanceEditResult(oldAttendance, newAttendance);

    }

    // 이름 오류 찾기 - public (UI 단계에서 제어)
    public void validateRegisteredName(String name){
        findStudent(name);
    }

    // 주말
    public void validateAttendableDate(){
        validateWeekday();
    }

    // 날짜 확인
    public void validateDayOfMonth(int day) {
        dayLimit(day);
    }


    // 주말 여부 확인
    private void validateWeekday() {
        LocalDate today = DateTimes.now().toLocalDate();
        DayOfWeek dow = today.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            String formatted = today.format(DateTimeFormatter.ofPattern("M월 d일 E요일"));
            throw new IllegalArgumentException("[ERROR] " + formatted + "은 등교일이 아닙니다.");
        }
    }

    private void dayLimit(int day){
        LocalDate today = DateTimes.now().toLocalDate();
        if (day < 1 || day > today.lengthOfMonth()) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 날짜입니다.");
        }
    }


    // 등록된 학생 여부 확인
    private Student findStudent(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 등록되지 않은 닉네임입니다."));
    }

    //  오늘 출석했는지 아닌지 확인
    private void ensureNotAlreadyChecked(Student student, LocalDate date) {
        if (student.hasAttendance(date)) {
            throw new IllegalArgumentException("[ERROR] 이미 출석 확인하였습니다. 필요한 경우 수정 기능을 이용해주세요.");
        }
    }

    // 시간 HH:mm로 파싱하기
    private LocalTime parseTime(String timeText){
        try {
            return LocalTime.parse(timeText, timeFormatter); // HH:mm
        } catch (Exception e) {
            throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다. 예: 09:00, 13:30");
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
