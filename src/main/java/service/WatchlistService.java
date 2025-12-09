package service;

import domain.Attendance;
import domain.AttendanceStatus;
import domain.Student;
import domain.WarningStatus;
import record.AttendanceStats;
import repository.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// 기능 4
public class WatchlistService {

    private final StudentRepository repository;

    public WatchlistService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<RiskEntry> buildMonthlyWatchlist(){
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<RiskEntry> entries = new ArrayList<>();

        for (Student student : repository.findAll()) {
            List<Attendance> monthly = filterMonthly(student.getAttendanceRecords(), year, month);
            if (monthly.isEmpty()) {
                continue;
            }

            AttendanceStats raw = countStats(monthly);
            AttendanceStats adjusted = raw.adjusted();
            WarningStatus level = WarningStatus.fromCounts(adjusted.absent(), adjusted.late());

            if(level == WarningStatus.NONE) {
                continue;
            }

            entries.add(new RiskEntry(
                    student.getName(),
                    adjusted.present(),
                    adjusted.late(),
                    adjusted.absent(),
                    level
            ));
        }

        sortEntries(entries);
        return entries;
    }

    private List<Attendance> filterMonthly(List<Attendance> all, int year, int month){
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : all) {
            if(a.getDate().getYear() == year
                && a.getDate().getMonthValue() == month){
                result.add(a);
            }
        }
        return result;
    }

    private List<Attendance> filterUntilYesterday(List<Attendance> all) {
        List<Attendance> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Attendance a : all) {
            LocalDate date = a.getDate();

            if (date.isBefore(today)) {
                result.add(a);
            }
        }

        return result;
    }

    private AttendanceStats countStats(List<Attendance> records) {
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

    private void sortEntries(List<RiskEntry> entries) {
        entries.sort(
                Comparator
                        .comparingInt((RiskEntry e) -> e.getLevel().getPriority())
                        .thenComparing(Comparator.comparingInt(RiskEntry::getAbsent).reversed())
                        .thenComparing(Comparator.comparingInt(RiskEntry::getLate).reversed())
                        .thenComparing(RiskEntry::getName)
        );
    }

}
