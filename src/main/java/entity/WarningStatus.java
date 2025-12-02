package entity;

public enum WarningStatus {
    NONE("정상"),
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

    public int getPriority(){
        return switch (this){
            case DISMISS -> 0;
            case MEETING -> 1;
            case WARNING -> 2;
            case NONE -> 3; // 안 씀.
        };
    }



}
