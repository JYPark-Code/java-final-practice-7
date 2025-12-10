package attendance.command;

import attendance.domain.*;
import attendance.formatter.AttendanceFormatter;
import attendance.formatter.ReportFormatter;
import attendance.formatter.WatchlistFormatter;
import attendance.service.*;
import attendance.service.AttendanceService.AttendanceEditResult;

import java.time.LocalDate;
import java.util.List;

public class Command {

    private final AttendanceService attendanceService;
    private final ReportService reportService;
    private final WatchlistService watchlistService;

    public Command(AttendanceService attendanceService,
                   ReportService reportService,
                   WatchlistService watchlistService) {
        this.attendanceService = attendanceService;
        this.reportService = reportService;
        this.watchlistService = watchlistService;
    }

    public void validateNickname(String name) {
        attendanceService.validateRegisteredName(name);
    }

    public void validateAttendableDate() {
        attendanceService.validateAttendableDate(LocalDate.now());
    }

    /**
     * 1번 커맨드 - 출석
     *
     * @param name
     * @param time
     * @return AttendanceFormatter.format(attendance)
     *
     * 닉네임을 입력해 주세요.
     * 이든
     * 등교 시간을 입력해 주세요.
     * 09:59
     *
     * 12월 13일 금요일 09:59 (출석)
     *
     */
    public String c1_attendance(String name, String time){
        Attendance attendance = attendanceService.recordAttendance(name, time);
        return AttendanceFormatter.format(attendance);
    }


    /**
     *  2. 출석 수정
     * @param name
     * @param date
     * @param time
     * @return String
     *
     * 출석을 수정하려는 크루의 닉네임을 입력해 주세요.
     * 빙티
     * 수정하려는 날짜(일)를 입력해 주세요.
     * 3
     * 언제로 변경하겠습니까?
     * 09:58
     *
     * 12월 03일 화요일 10:07 (지각) -> 09:58 (출석) 수정 완료!
     */
    public String c2_edit(String name, int date, String time){
        AttendanceEditResult result = attendanceService.editAttendance(name, date, time);

        String oldText = AttendanceFormatter.format(result.getOldAttendance());
        String newText = AttendanceFormatter.formatTimeAndStatus(result.getNewAttendance());

        return oldText + " -> " + newText + " 수정 완료!";
    }


    /**
     * 3. 학생 개인 출결 리포트
     * @param name
     * @return String
     */
    public String c3_report(String name){
        MonthlyReport report = reportService.generateMonthlyReport(name);
        return ReportFormatter.format(report);
    }

    /**
     * 전체 학생 제적 위험 리스트
     * @return String
     */
    // 4. 제적 위험자 리스트
    public String c4_watchlist(){
        List<RiskEntry> entries = watchlistService.buildMonthlyWatchlist();
        return WatchlistFormatter.format(entries);
    }


}
