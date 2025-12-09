package service;

import domain.Attendance;
import domain.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WatchlistServiceTest {

    @Test
    @DisplayName("저장소에 학생이 없으면 제적 위험자 리스트는 비어 있다")
    void buildMonthlyWatchlist_empty_when_no_students() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        WatchlistService service = new WatchlistService(repo);

        // when
        List<RiskEntry> result = service.buildMonthlyWatchlist();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이번 달 출석이 없거나 경고 수준이 NONE인 학생은 리스트에 포함되지 않는다")
    void buildMonthlyWatchlist_excludes_students_without_risk() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        WatchlistService service = new WatchlistService(repo);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // 1) 이번 달 출석이 전혀 없는 학생
        Student noRecord = new Student("철수");
        // 지난달 출석만 하나 넣어둔다 (이번달 필터에서 제외되어야 함)
        noRecord.addAttendance(Attendance.fromArrival(
                today.minusMonths(1),
                LocalTime.of(10, 0),
                LocalTime.of(9, 55)
        ));
        repo.save(noRecord);

        // 2) 이번 달 출석은 있지만 지각/결석 없이 모두 정상 출석인 학생 (WarningStatus.NONE)
        Student safe = new Student("영희");
        safe.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 1),
                LocalTime.of(10, 0),
                LocalTime.of(9, 55)   // PRESENT
        ));
        safe.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 2),
                LocalTime.of(10, 0),
                LocalTime.of(9, 50)   // PRESENT
        ));
        repo.save(safe);

        // when
        List<RiskEntry> result = service.buildMonthlyWatchlist();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("경고 수준이 NONE이 아닌 학생들만 제적 위험자 리스트에 포함되고, 우선순위대로 정렬된다")
    void buildMonthlyWatchlist_includes_only_risky_students_and_sorts() {
        // given
        StudentRepository repo = new InMemoryStudentRepository();
        WatchlistService service = new WatchlistService(repo);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // 결석 판정을 위해 사용할 수업 시작 시간
        LocalTime lessonStart = LocalTime.of(10, 0);

        // A: 결석 5회 → DISMISS
        Student a = new Student("A학생");
        for (int i = 1; i <= 5; i++) {
            a.addAttendance(Attendance.fromArrival(
                    LocalDate.of(year, month, i),
                    lessonStart,
                    lessonStart.plusMinutes(40)  // 30분 이상 → ABSENT
            ));
        }
        repo.save(a);

        // B: 결석 3회 → MEETING
        Student b = new Student("B학생");
        for (int i = 1; i <= 3; i++) {
            b.addAttendance(Attendance.fromArrival(
                    LocalDate.of(year, month, 10 + i),
                    lessonStart,
                    lessonStart.plusMinutes(40)  // ABSENT
            ));
        }
        repo.save(b);

        // C: 결석 2회 → WARNING
        Student c = new Student("C학생");
        for (int i = 1; i <= 2; i++) {
            c.addAttendance(Attendance.fromArrival(
                    LocalDate.of(year, month, 20 + i),
                    lessonStart,
                    lessonStart.plusMinutes(40)  // ABSENT
            ));
        }
        repo.save(c);

        // D: 이번 달 결석/지각 없는 학생 → NONE, 리스트에 포함되면 안 됨
        Student d = new Student("D학생");
        d.addAttendance(Attendance.fromArrival(
                LocalDate.of(year, month, 1),
                lessonStart,
                lessonStart.minusMinutes(5) // PRESENT
        ));
        repo.save(d);

        // when
        List<RiskEntry> result = service.buildMonthlyWatchlist();

        // then
        // 1) 위험 학생(A,B,C)만 포함되어야 한다
        assertThat(result)
                .hasSize(3)
                .extracting(RiskEntry::getName)
                .containsExactlyInAnyOrder("A학생", "B학생", "C학생");

        // 2) 정렬 순서를 확인한다.
        // WarningStatus.getPriority()가 DISMISS < MEETING < WARNING 순으로 정의되어 있다면
        // 리스트는 A(제일 위험) → B → C 순이 되어야 한다.
        RiskEntry first = result.get(0);
        RiskEntry second = result.get(1);
        RiskEntry third = result.get(2);

        assertThat(first.getName()).isEqualTo("A학생");
        assertThat(second.getName()).isEqualTo("B학생");
        assertThat(third.getName()).isEqualTo("C학생");

        // 3) 각 엔트리의 absent 값도 기대한 만큼 집계되었는지 확인
        assertThat(first.getAbsent()).isEqualTo(5);
        assertThat(second.getAbsent()).isEqualTo(3);
        assertThat(third.getAbsent()).isEqualTo(2);
    }

}
