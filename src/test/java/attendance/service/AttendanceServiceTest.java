package attendance.service;

import attendance.domain.Attendance;
import attendance.domain.AttendanceStatus;
import attendance.domain.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import attendance.repository.InMemoryStudentRepository;
import attendance.repository.StudentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class AttendanceServiceTest {

    @Test
    @DisplayName("등록되지 않은 학생은 출석할 수 없다")
    void recordAttendance_fail_when_student_not_exists() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        AttendanceService service = new AttendanceService(repo);

        // when & then
        assertThatThrownBy(() ->
                service.recordAttendance("철수", "10:00")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 이름");
    }

    @Test
    @DisplayName("평일에 등록된 학생은 정상적으로 출석할 수 있다")
    void recordAttendance_success_when_weekday_and_student_exists() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        AttendanceService service = new AttendanceService(repo);

        Student student = new Student("영희");
        repo.save(student);

        // 오늘이 주말이면 테스트 자체가 의미 없어서 방어
        LocalDate today = LocalDate.now();
        DayOfWeek dow = today.getDayOfWeek();
        assumeTrue(dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY);

        // when
        Attendance attendance = service.recordAttendance("영희", "10:00");

        // then
        Student reloaded = repo.findByName("영희").orElseThrow();

        assertThat(reloaded.getAttendanceRecords())
                .hasSize(1)
                .contains(attendance);
    }

    @Test
    @DisplayName("같은 날 두 번 출석하면 예외가 발생한다")
    void recordAttendance_fail_when_already_checked_today() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        AttendanceService service = new AttendanceService(repo);

        Student student = new Student("민수");
        repo.save(student);

        LocalDate today = LocalDate.now();
        assumeTrue(today.getDayOfWeek() != DayOfWeek.SATURDAY
                && today.getDayOfWeek() != DayOfWeek.SUNDAY);

        service.recordAttendance("민수", "10:00");

        // when & then
        assertThatThrownBy(() ->
                service.recordAttendance("민수", "10:10")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 출석");
    }

    @Test
    @DisplayName("editAttendance는 기존 출석을 수정하고 이전/이후 출석을 모두 반환한다")
    void editAttendance_success() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        AttendanceService service = new AttendanceService(repo);

        Student student = new Student("수지");
        repo.save(student);

        LocalDate today = LocalDate.now();
        assumeTrue(today.getDayOfWeek() != DayOfWeek.SATURDAY
                && today.getDayOfWeek() != DayOfWeek.SUNDAY);

        LocalTime lessonStart;
        if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
            lessonStart = LocalTime.of(13, 0);
        } else {
            lessonStart = LocalTime.of(10, 0);
        }

        Attendance original = Attendance.fromArrival(
                today,
                lessonStart,
                LocalTime.of(10, 20) // 지각
        );
        student.addAttendance(original);

        // when
        AttendanceService.AttendanceEditResult result =
                service.editAttendance("수지", today.getDayOfMonth(), "09:55");

        Attendance oldAttendance = result.getOldAttendance();
        Attendance newAttendance = result.getNewAttendance();

        // then
        assertThat(oldAttendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(newAttendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);

        Student reloaded = repo.findByName("수지").orElseThrow();

        assertThat(reloaded.getAttendanceRecords())
                .hasSize(1)
                .first()
                .satisfies(a -> {
                    assertThat(a.getArrivalTime()).isEqualTo(LocalTime.of(9, 55));
                    assertThat(a.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
                });
    }

    @Test
    @DisplayName("editAttendance는 해당 날짜에 출석이 없으면 예외를 던진다")
    void editAttendance_fail_when_no_attendance_on_that_day() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        AttendanceService service = new AttendanceService(repo);

        Student student = new Student("하루");
        repo.save(student);

        LocalDate today = LocalDate.now();

        // when & then
        assertThatThrownBy(() ->
                service.editAttendance("하루", today.getDayOfMonth(), "10:00")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("출석하지 않았습니다");
    }
}
