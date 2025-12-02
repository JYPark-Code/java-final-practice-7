package entity;

public enum WarningStatus {
    NONE("정상",3),
    WARNING("경고",2), //  - 결석 2회 이상
    MEETING("면담",1), //  - 결석 3회 이상
    DISMISS("제적",0); //  - 결석 5회 이상

    private final String text;
    private final int priority;

    WarningStatus(String text, int priority){
        this.text = text;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public int getPriority(){
        return priority;
    }

    // 지각 -> 결석 변환하는 메소드 (커맨드에서 쓰진 않았음)
    public static WarningStatus fromCounts(int absent, int late) {
        int adjustedAbsent = absent + (late / 3);

        if (adjustedAbsent >= 5) return DISMISS;
        if (adjustedAbsent >= 3) return MEETING;
        if (adjustedAbsent == 2) return WARNING;
        return NONE;
    }

}

