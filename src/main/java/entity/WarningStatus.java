package entity;

public enum WarningStatus {
    WARNING("경고"), //  - 결석 2회 이상
    MEETING("면담"), //  - 결석 3회 이상
    DISMISS("제적"); //  - 결석 5회 이상

    private final String text;

    WarningStatus(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
