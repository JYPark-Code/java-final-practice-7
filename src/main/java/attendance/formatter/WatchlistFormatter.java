package attendance.formatter;

import attendance.service.RiskEntry;

import java.util.List;

public class WatchlistFormatter {

    private WatchlistFormatter() {

    }

    public static String format(List<RiskEntry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("제적 위험자 조회 결과\n");

        if (entries.isEmpty()) {
            sb.append("- 제적 위험자가 없습니다.");
            return sb.toString();
        }

        for (RiskEntry e : entries) {
            sb.append("- ")
                    .append(e.getName())
                    .append(": 결석 ").append(e.getAbsent()).append("회, ")
                    .append("지각 ").append(e.getLate()).append("회 ")
                    .append("(").append(e.getLevel().getText()).append(")")
                    .append("\n");
        }

        return sb.toString();
    }

}
