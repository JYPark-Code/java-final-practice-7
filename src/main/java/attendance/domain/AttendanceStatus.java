package attendance.domain;

public enum AttendanceStatus {
    PRESENT("출석"),
    LATE("지각"),
    ABSENT("결석");

    private final String text;

    AttendanceStatus(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
