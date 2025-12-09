package service;

import domain.Attendance;
import domain.Student;
import domain.WarningStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import record.AttendanceStats;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ReportServiceTest {

    @Test
    @DisplayName("등록되지 않은 학생으로 리포트를 조회하면 예외가 발생한다")
    void generateMonthlyReport_fail_when_student_not_exists() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        ReportService service = new ReportService(repo);

        // when & then
        assertThatThrownBy(() ->
                service.generateMonthlyReport("없는사람")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 이름");
    }

    @Test
    @DisplayName("이번 달 출석 기록이 하나도 없으면 stats는 0,0,0이고 warning은 NONE이다")
    void generateMonthlyReport_returns_empty_stats_when_no_records() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        ReportService service = new ReportService(repo);

        Student student = new Student("철수");
        repo.save(student);

        // when
        MonthlyReport report = service.generateMonthlyReport("철수");

        // then
        AttendanceStats stats = report.getStats();

        assertThat(report.getName()).isEqualTo("철수");
        assertThat(stats.present()).isZero();
        assertThat(stats.late()).isZero();
        assertThat(stats.absent()).isZero();
        assertThat(report.getLevel()).isEqualTo(WarningStatus.NONE);
        assertThat(report.getRecords()).isEmpty();
    }

    @Test
    @DisplayName("이번 달 출석 기록만 필터링하여 통계가 정확히 계산된다")
    void generateMonthlyReport_counts_only_this_month_records() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        ReportService service = new ReportService(repo);

        Student student = new Student("영희");
        repo.save(student);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // ✅ 이번 달 출석 3개
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 1),
                LocalTime.of(10, 0),
                LocalTime.of(9, 55)   // PRESENT
        ));
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 2),
                LocalTime.of(10, 0),
                LocalTime.of(10, 10)  // LATE
        ));
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 3),
                LocalTime.of(10, 0),
                LocalTime.of(10, 40)  // ABSENT
        ));

        // ✅ 지난 달 출석 1개 (집계되면 안 됨)
        student.addAttendance(Attendance.fromArrival(
                today.minusMonths(1),
                LocalTime.of(10, 0),
                LocalTime.of(9, 50)
        ));

        // when
        MonthlyReport report = service.generateMonthlyReport("영희");
        AttendanceStats stats = report.getStats();

        // then
        assertThat(report.getRecords()).hasSize(3);
        assertThat(stats.present()).isEqualTo(1);
        assertThat(stats.late()).isEqualTo(1);
        assertThat(stats.absent()).isEqualTo(1);
    }

    @Test
    @DisplayName("결석과 지각 수에 따라 WarningStatus가 정확히 계산된다")
    void generateMonthlyReport_calculates_warning_status_correctly() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        ReportService service = new ReportService(repo);

        Student student = new Student("민수");
        repo.save(student);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // ✅ 결석 3회, 지각 1회 → 정책상 MEETING or WARNING (네 정책에 맞게)
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 1),
                LocalTime.of(10, 0),
                LocalTime.of(10, 40)  // ABSENT
        ));
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 2),
                LocalTime.of(10, 0),
                LocalTime.of(10, 50)  // ABSENT
        ));
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 3),
                LocalTime.of(10, 0),
                LocalTime.of(10, 45)  // ABSENT
        ));
        student.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 4),
                LocalTime.of(10, 0),
                LocalTime.of(10, 10)  // LATE
        ));

        // when
        MonthlyReport report = service.generateMonthlyReport("민수");

        // then
        assertThat(report.getStats().absent()).isEqualTo(3);
        assertThat(report.getStats().late()).isEqualTo(1);

        // ⚠️ 이 부분은 네 WarningStatus 규칙에 맞게 기대값 조정
        assertThat(report.getLevel())
                .isIn(WarningStatus.WARNING, WarningStatus.MEETING);
    }

}
