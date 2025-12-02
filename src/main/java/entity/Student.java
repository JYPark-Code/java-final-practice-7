package entity;

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
}
