package attendance.service;

import attendance.domain.Attendance;
import attendance.domain.AttendanceStatus;
import attendance.domain.Student;
import attendance.domain.WarningStatus;
import attendance.record.AttendanceStats;
import attendance.repository.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 기능 3 개인 리포트 만들기
public class ReportService {

    private final StudentRepository repository;

    public ReportService(StudentRepository repository) {
        this.repository = repository;
    }

    public MonthlyReport generateMonthlyReport(String name){
        Student student = findStudent(name);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<Attendance> records = filterMonthly(student.getAttendanceRecords(), year, month);

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
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));
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

}
