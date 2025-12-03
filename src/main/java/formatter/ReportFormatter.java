package formatter;

import domain.Attendance;
import domain.WarningStatus;
import record.AttendanceStats;
import service.MonthlyReport;

public class ReportFormatter {

    private ReportFormatter(){
    }

    public static String format(MonthlyReport report){
        String name = report.getName();

        if(report.isEmpty()){
            return "이번 달 " + name + "의 출석 기록이 없습니다.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("이번 달 ").append(name).append("의 출석 기록입니다. \n\n");

        for (Attendance a : report.getRecords()) {
            sb.append(AttendanceFormatter.format(a)).append("\n");
        }

        sb.append("\n");

        AttendanceStats stats = report.getStats();
        sb.append("출석: ").append(stats.present()).append("회\n");
        sb.append("지각: ").append(stats.late()).append("회\n");
        sb.append("결석: ").append(stats.absent()).append("회\n");

        WarningStatus level = report.getLevel();
        if (level == WarningStatus.DISMISS) {
            sb.append("\n제적 대상자입니다.\n");
        } else if (level == WarningStatus.MEETING) {
            sb.append("\n면담 대상자입니다.\n");
        } else if (level == WarningStatus.WARNING) {
            sb.append("\n경고 대상자입니다.\n");
        }

        return sb.toString();
    }

}
