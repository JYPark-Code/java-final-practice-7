package attendance.service;

import attendance.domain.Attendance;
import attendance.domain.AttendanceStatus;
import attendance.domain.Student;
import attendance.domain.WarningStatus;
import attendance.record.AttendanceStats;
import attendance.repository.StudentRepository;
import camp.nextstep.edu.missionutils.DateTimes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static attendance.util.CsvLoader.getLessonStart;

// 기능 3인 리포트 만들기
public class ReportService {

    private final StudentRepository repository;

    public ReportService(StudentRepository repository) {
        this.repository = repository;
    }

    public MonthlyReport generateMonthlyReport(String name){
        Student student = findStudent(name);

        LocalDate today = DateTimes.now().toLocalDate();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<Attendance> originalRecords = filterMonthly(student.getAttendanceRecords(), year, month);
        List<Attendance> records = fillAbsentDaysUntilYesterday(originalRecords, name, year, month);

        if(records.isEmpty()){
            AttendanceStats emptyStats = new AttendanceStats(0, 0, 0);
            return new MonthlyReport(name, year, month, records, emptyStats, WarningStatus.NONE);
        }

        AttendanceStats raw = countStats(records);
        AttendanceStats adjusted = raw.adjusted();
        WarningStatus level = WarningStatus.fromCounts(adjusted.absent(), adjusted.late());

        return new MonthlyReport(name, year, month, records, adjusted, level);
    }

    private Student findStudent(String name){
        return repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 등록되지 않은 닉네임입니다."));
    }

    private List<Attendance> filterMonthly(List<Attendance> all, int year, int month){
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : all){
            if(a.getDate().getYear() == year &&
                a.getDate().getMonthValue() == month) {
                result.add(a);
            }
        }
        // 날짜 순서대로
        result.sort((a1, a2) -> a1.getDate().compareTo(a2.getDate()));
        return result;
    }

    private AttendanceStats countStats(List<Attendance> records){
        int present = 0;
        int late = 0;
        int absent = 0;

        for (Attendance a : records) {
            AttendanceStatus status = a.getStatus();
            if (status == AttendanceStatus.PRESENT) {
                present++;
            } else if (status == AttendanceStatus.LATE) {
                late++;
            } else if (status == AttendanceStatus.ABSENT) {
                absent++;
            }
        }

        return new AttendanceStats(present, late, absent);
    }

    // 결석 기록 만들기
    private List<Attendance> fillAbsentDaysUntilYesterday(
            List<Attendance> existing,
            String name,
            int year,
            int month
    ) {
        Map<LocalDate, Attendance> byDate = new HashMap<>();
        for (Attendance a : existing){
            byDate.put(a.getDate(), a);
        }

        List<Attendance> result = new ArrayList<>();

        LocalDate today = DateTimes.now().toLocalDate();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = today.minusDays(1);

        if (end.isBefore(start)) {
            // 오늘이 1일이면 → 이번 달은 아직 집계할 게 없음
            return result;
        }

//        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        for(LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            DayOfWeek dow = date.getDayOfWeek();

            if(dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                continue; // 주말 제외
            }

            if(byDate.containsKey(date)){
                // 출석 데이터
                result.add(byDate.get(date));
            } else {
                // 기록 없는 평일 -> 무단 결석
                LocalTime lessonStart = getLessonStart(date);
                result.add(Attendance.absentWithoutCheck(date, lessonStart));
            }

        }

        result.sort(Comparator.comparing(Attendance::getDate));
        return result;
    }

}
