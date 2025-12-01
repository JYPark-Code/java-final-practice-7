package entity;

public enum AttendanceStatus {
    PRESENT("출석"),
    LATE("지각"),
    ABSENT("결석");

    private final String status;

    AttendanceStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
