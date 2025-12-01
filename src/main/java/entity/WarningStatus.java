package entity;

public enum WarningStatus {
    WARNING("경고"), //  - 결석 2회 이상
    MEETING("면담"), //  - 결석 3회 이상
    DISMISS("제적"); //  - 결석 5회 이상

    private final String status;

    WarningStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
