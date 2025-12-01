package command;

import entity.Attendance;
import entity.Student;
import repository.StudentRepository;
import entity.AttendanceFormatter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Command {

    private final StudentRepository repository;

    public Command(StudentRepository repository) {
        this.repository = repository;
    }

    /**
     * 1번 커맨드 - 출석
     *
     * @param name
     * @param time
     * @return AttendanceFormatter.format(attendance)
     *
     * 닉네임을 입력해 주세요.
     * 이든
     * 등교 시간을 입력해 주세요.
     * 09:59
     *
     * 12월 13일 금요일 09:59 (출석)
     *
     */
    public String c1_attendance(String name, String time){

        // 1. 학생 찾기
        Student student = repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));

        // 2. 오늘 날짜
        LocalDate today = LocalDate.now();
        LocalTime lessonStart = getLessonStart(today);

        // 3. 출석 중복 확인
        if(student.hasAttendance(today)){
            throw new IllegalArgumentException("이미 출석 확인하였습니다. 필요한 경우 수정 기능을 이용해주세요.");
        }

        // 4. 등교 시간
        LocalTime arrivalTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));


        // 5. Attendance 생성
        Attendance attendance = Attendance.fromArrival(today, lessonStart, arrivalTime);

        // 6. 학생 출석 기록 추가
        student.addAttendance(attendance);

        return AttendanceFormatter.format(attendance);
    }

    private LocalTime getLessonStart(LocalDate date) {
        // 월 13:00, 화~금 10:00
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.MONDAY){
            return LocalTime.of(13, 0);
        }
        return LocalTime.of(10, 0);
    }

}
