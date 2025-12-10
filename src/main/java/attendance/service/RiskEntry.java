package attendance.service;

import attendance.domain.WarningStatus;

public class RiskEntry {

    private final String name;
    private final int present;
    private final int late;
    private final int absent;
    private final WarningStatus level;

    public RiskEntry(String name,
                     int present,
                     int late,
                     int absent,
                     WarningStatus level) {
        this.name = name;
        this.present = present;
        this.late = late;
        this.absent = absent;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getPresent() {
        return present;
    }

    public int getLate() {
        return late;
    }

    public int getAbsent() {
        return absent;
    }

    public WarningStatus getLevel() {
        return level;
    }
}
