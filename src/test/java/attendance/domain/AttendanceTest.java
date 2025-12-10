package attendance.domain;

import attendance.Application;
import attendance.formatter.AttendanceFormatter;
import org.junit.jupiter.api.DisplayName;
import camp.nextstep.edu.missionutils.test.NsTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AttendanceTest extends NsTest {

    @Test
    void 잘못된_형식_예외_테스트()  {
        assertNowTest(
                () -> assertThatThrownBy(() -> run("1", "짱수", "33:71"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("[ERROR] 잘못된 형식을 입력하였습니다."),
                LocalDate.of(2024, 12, 13).atStartOfDay()
        );
    }

    @Test
    void 등록되지_않은_닉네임_예외_테스트() {
        assertNowTest(
                () -> assertThatThrownBy(() -> run("1", "빈봉"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("[ERROR] 등록되지 않은 닉네임입니다."),
                LocalDate.of(2024, 12, 13).atStartOfDay()
        );
    }


    @Test
    void 주말_또는_공휴일_예외_테스트() {
        assertNowTest(
                () -> assertThatThrownBy(() -> run("1"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("[ERROR] 12월 14일 토요일은 등교일이 아닙니다."),
                LocalDate.of(2024, 12, 14).atStartOfDay()
        );
    }

    @Test
    void 출석_확인_기능_테스트() {
        assertNowTest(
                () -> {
                    runException("1", "짱수", "08:00");
                    assertThat(output()).contains("12월 13일 금요일 08:00 (출석)");
                },
                LocalDate.of(2024, 12, 13).atStartOfDay()
        );
    }

    @Test
    void 출석_수정_및_크루별_출석_기록_확인_기능_테스트() {
        assertNowTest(
                () -> {
                    runException("2", "짱수", "12", "10:31", "3", "짱수");
                    assertThat(output()).contains(
                            "12월 12일 목요일 10:00 (출석) -> 10:31 (결석) 수정 완료!",
                            "12월 02일 월요일 13:00 (출석)",
                            "12월 03일 화요일 10:00 (출석)",
                            "12월 04일 수요일 10:00 (출석)",
                            "12월 05일 목요일 10:00 (출석)",
                            "12월 06일 금요일 10:00 (출석)",
                            "12월 09일 월요일 13:00 (출석)",
                            "12월 10일 화요일 10:00 (출석)",
                            "12월 11일 수요일 --:-- (결석)",
                            "12월 12일 목요일 10:31 (결석)",
                            "출석: 7회",
                            "지각: 0회",
                            "결석: 2회",
                            "경고 대상자"
                    );
                },
                LocalDate.of(2024, 12, 13).atStartOfDay()
        );
    }


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


    @Override
    protected void runMain() {
        try {
            Application.main(new String[]{});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


