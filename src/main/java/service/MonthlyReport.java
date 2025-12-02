package service;

import entity.Attendance;
import entity.WarningStatus;
import record.AttendanceStats;

import java.util.List;

public class MonthlyReport {

    private final String name;
    private final int year;
    private final int month;
    private final List<Attendance> records;
    private final AttendanceStats stats;
    private final WarningStatus level;

    public MonthlyReport(String name,
                         int year,
                         int month,
                         List<Attendance> records,
                         AttendanceStats stats,
                         WarningStatus level) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.records = records;
        this.stats = stats;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public List<Attendance> getRecords() {
        return records;
    }

    public AttendanceStats getStats() {
        return stats;
    }

    public WarningStatus getLevel() {
        return level;
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

}
