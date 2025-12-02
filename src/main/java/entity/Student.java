package entity;

import record.AttendanceStats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student {

    private final String name;
    private final List<Attendance> attendanceRecords = new ArrayList<>();

    public Student(String name) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Attendance> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void addAttendance(Attendance record){
        if(hasAttendance(record.getDate())){
            throw new IllegalArgumentException("이미 해당 날짜의 출석이 있습니다.");
        }
        attendanceRecords.add(record);
    }

    public Attendance editAttendance(LocalDate date, Attendance newRecord){
        for(int i = 0; i < attendanceRecords.size(); i++){
            Attendance a = attendanceRecords.get(i);
            if(a.getDate().equals(date)){
                attendanceRecords.set(i, newRecord);
                return a;
            }
        }
        throw new IllegalArgumentException("해당 날짜의 출석 기록이 없습니다.");
    }

    public boolean hasAttendance(LocalDate date) {
        for (Attendance a : attendanceRecords) {
            if(a.getDate().equals(date)){
                return true;
            }
        }
        return false;
    }

    /**
     * Command에 작성했던 엔티티 책임 로직
     */
    public List<Attendance> getMonthlyRecords(int year, int month){
        List<Attendance> monthly = new ArrayList<>();
        for(Attendance a : attendanceRecords){
            if(a.getDate().getYear() == year && a.getDate().getMonthValue() == month) {
                monthly.add(a);
            }
        }
        return monthly;
    }

    /**
     *  개인 출석 통계
     */
    public AttendanceStats getMonthlyStats(int year, int month){
        List<Attendance> monthly = getMonthlyRecords(year, month);

        int present = 0;
        int late = 0;
        int absent = 0;

        for(Attendance a : monthly){
            if(a.isPresent()){
                present++;
            } else if(a.isLate()){
                late++;
            } else if(a.isAbsent()){
                absent++;
            }
        }
        return new AttendanceStats(present, late, absent).adjusted();
    }

    /**
     * 위험도 반환
     */
    public WarningStatus getRiskLevel(int year, int month){
        AttendanceStats stats = getMonthlyStats(year, month);
        return WarningStatus.fromCounts(stats.absent(), stats.late());
    }



}
