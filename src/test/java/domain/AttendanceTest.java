package domain;

import formatter.AttendanceFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AttendanceTest {

    @Test
    @DisplayName("수업 시작 전에 도착하면 출석(PRESENT)이다")
    void present_arrived_b4_lesson_start(){
        //given
        LocalDate date = LocalDate.of(2025,12,9);
        LocalTime lessonStart = LocalTime.of(10, 0);
        LocalTime arrival = LocalTime.of(9,55);

        //when
        Attendance attendance = Attendance.fromArrival(date, lessonStart, arrival);

        //then
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(attendance.isPresent()).isTrue();
        assertThat(attendance.isLate()).isFalse();
        assertThat(attendance.isAbsent()).isFalse();

    }

    @Test
    @DisplayName("수업 시작 후 5분 이내 도착은 출석(PRESENT)이다")
    void present_when_arrive_within_five_minutes() {
        // given
        LocalTime lessonStart = LocalTime.of(10, 0);
        LocalTime arrival = LocalTime.of(10, 5);

        // when
        Attendance attendance = Attendance.fromArrival(
                LocalDate.now(), lessonStart, arrival
        );

        // then
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    @DisplayName("수업 시작 5분 초과 30분 이하이면 지각(LATE)이다")
    void late_when_arrive_after_five_minutes() {
        // given
        LocalTime lessonStart = LocalTime.of(10, 0);
        LocalTime arrival = LocalTime.of(10, 30);

        // when
        Attendance attendance = Attendance.fromArrival(
                LocalDate.now(), lessonStart, arrival
        );

        // then
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(attendance.isLate()).isTrue();
    }

    @Test
    @DisplayName("수업 시작 30분 초과 늦으면 결석(ABSENT)이다")
    void absent_when_arrive_after_thirty_minutes() {
        // given
        LocalTime lessonStart = LocalTime.of(10, 0);
        LocalTime arrival = LocalTime.of(10, 31); //

        // when
        Attendance attendance = Attendance.fromArrival(
                LocalDate.now(), lessonStart, arrival
        );

        // then
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
        assertThat(attendance.isAbsent()).isTrue();
    }

    @Test
    @DisplayName("무단 결석은 arrivalTime이 null이고 상태는 ABSENT이다")
    void absent_without_check() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 13);
        LocalTime lessonStart = LocalTime.of(10, 0);

        // when
        Attendance attendance = Attendance.absentWithoutCheck(date, lessonStart);

        // then
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
        assertThat(attendance.getArrivalTime()).isNull();
        assertThat(attendance.isAbsent()).isTrue();
    }

    @Test
    @DisplayName("arrivalTime이 null이면 fromArrival은 예외를 던진다")
    void throw_exception_when_arrival_time_is_null() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 13);
        LocalTime lessonStart = LocalTime.of(10, 0);

        // when & then
        assertThatThrownBy(() ->
                Attendance.fromArrival(date, lessonStart, null)
        )
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("arrivalTime");
    }

    @Test
    @DisplayName("무단 결석(ABSENT)은 출력 시 시간은 --:-- 로 표시된다")
    void format_absent_without_time_should_render_dash() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 13);
        LocalTime lessonStart = LocalTime.of(10, 0);

        Attendance attendance = Attendance.absentWithoutCheck(date, lessonStart);

        // when
        String result = AttendanceFormatter.format(attendance);

        // then
        assertThat(result).contains("--:--");
        assertThat(result).contains(AttendanceStatus.ABSENT.getText());
    }


}


