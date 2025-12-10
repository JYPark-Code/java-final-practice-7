package attendance.record;

public record AttendanceStats (int present, int late, int absent) {

    public AttendanceStats adjusted(){
        int regardAbsent = late / 3;
        int totalAbsent = absent + regardAbsent;
        int remainingLate = late % 3;

        return new AttendanceStats(present, remainingLate, totalAbsent);
    }

}

