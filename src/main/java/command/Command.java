package command;

import entity.*;
import repository.StudentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Command {

    private final StudentRepository repository;

    public Command(StudentRepository repository) {
        this.repository = repository;
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

        // 1. 오늘 날짜
        LocalDate today = LocalDate.now();
        LocalTime lessonStart = getLessonStart(today);
        DayOfWeek dow = today.getDayOfWeek();

        // 주말 제한
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("[ERROR] 주말에는 출석을 받지 않습니다.");
        }

        // 2. 학생 찾기
        Student student = repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));

        // 3. 출석 중복 확인
        if(student.hasAttendance(today)){
            throw new IllegalArgumentException("이미 출석 확인하였습니다. 필요한 경우 수정 기능을 이용해주세요.");
        }

        // 4. 등교 시간
        LocalTime arrivalTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        // 5. Attendance 생성
        Attendance attendance = Attendance.fromArrival(today, lessonStart, arrivalTime);

        // 6. 학생 출석 기록 추가
        student.addAttendance(attendance);

        return AttendanceFormatter.format(attendance);
    }

    private LocalTime getLessonStart(LocalDate date) {
        // 월 13:00, 화~금 10:00
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.MONDAY){
            return LocalTime.of(13, 0);
        }
        return LocalTime.of(10, 0);
    }

    /**
     *  2. 출석 수정
     * @param name
     * @param date
     * @param time
     * @return
     */
    public String c2_edit(String name, int date, String time){

        // 1. 학생 찾기
        Student student = repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));

        // 2. 수정할 날짜
        // 이번 달 + 입력받은 일(date)
        LocalDate today = LocalDate.now();
        LocalDate targetDate = LocalDate.of(today.getYear(), today.getMonth(), date);

        LocalTime lessonStart = getLessonStart(targetDate);

        // 3. 해당 출석 안 했을 경우
        if(!student.hasAttendance(targetDate)){
            throw new IllegalArgumentException("출석하지 않았습니다. 출석 기능을 이용해주세요.");
        }

        // 4. 등교 시간
        LocalTime arrivalTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        // 5. Attendance 생성
        Attendance newAttendance = Attendance.fromArrival(targetDate, lessonStart, arrivalTime);

        // 6. 이전 기록 가져오면서 수정
        Attendance oldAttendance =  student.editAttendance(targetDate, newAttendance);

        // 7. 포맷팅
        String oldText = AttendanceFormatter.format(oldAttendance);
        String newText = AttendanceFormatter.formatTimeAndStatus(newAttendance);

        return oldText + " -> " + newText + " 수정 완료!";

    }

    /**
     * 3. 학생 개인 출결 리포트
     * @param name
     * @return
     */
    public String c3_report(String name){
        // 1. 학생 찾기
        Student student = repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이름입니다."));

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // 2. 이번달 출석만 필터링, 날짜 순 정렬 (요구사항은 하루 전날까지인데, 금일이 12월 초라 테스트할려고 이렇게 뽑음)
        List<Attendance> monthly = student.getAttendanceRecords().stream()
                .filter(a -> a.getDate().getYear() == year && a.getDate().getMonthValue() == month)
                .sorted(Comparator.comparing(Attendance::getDate))
                .toList();

        // 하루 전까지라고 하면, 다음과 같다.
//        List<Attendance> filtered = student.getAttendanceRecords().stream()
//                .filter(a -> a.getDate().isBefore(today))  //  오늘 이전만!
//                .filter(a -> a.getDate().getYear() == year)
//                .filter(a -> a.getDate().getMonthValue() == month)
//                .sorted(Comparator.comparing(Attendance::getDate))
//                .toList();

        if (monthly.isEmpty()){
            return "이번 달 " + name + "의 출석 기록이 없습니다.";
        }

        // 3. 카운트 세기
        int presentCount = 0;
        int lateCount = 0;
        int absentCount = 0;

        for (Attendance a : monthly){
            switch (a.getStatus()) {
                case PRESENT -> presentCount++;
                case LATE -> lateCount++;
                case ABSENT -> absentCount++;
            }
        }

        // 지각 3회 -> 1회 결석
        // 🔥 지각 → 결석 변환
        int bonusAbsent = lateCount / 3;
        absentCount += bonusAbsent;
        lateCount = lateCount % 3;


        // 4) 메시지 조립
        StringBuilder sb = new StringBuilder();
        sb.append("이번 달 ").append(name).append("의 출석 기록입니다.\n\n");

        for (Attendance a : monthly) {
            sb.append(AttendanceFormatter.format(a)).append("\n");
        }

        sb.append("\n");
        sb.append("출석: ").append(presentCount).append("회\n");
        sb.append("지각: ").append(lateCount).append("회\n");
        sb.append("결석: ").append(absentCount).append("회\n");

        // 면담/경고

         if (absentCount >= 5) {
             sb.append("\n제적 대상자입니다.\n");
         } else if (absentCount >= 3) {
            sb.append("\n면담 대상자입니다.\n");
         } else if (absentCount >= 2) {
             sb.append("\n경고 대상자입니다.\n");
         }

        return sb.toString();

    }

    /**
     * 전체 학생 제적 위험 리스트
     * @return
     */
    public String c4_watchlist(){
        StringBuilder sb4 = new StringBuilder();
        sb4.append("제적 위험자 조회결과\n");

        LocalDate today = LocalDate.now();

        // 출력할 제적 위험 리스트
        List<RiskEntry> entries = new ArrayList<>();

        // 1. 모든 학생
        for (Student student : repository.findAll()){

            List<Attendance> all = student.getAttendanceRecords();
            List<Attendance> filtered = new ArrayList<>();

            for(Attendance a : all){
                LocalDate d = a.getDate();
                if (d.getYear() == today.getYear()
                        && d.getMonthValue() == today.getMonthValue()) {
                    filtered.add(a);
                }
            }

            if (filtered.isEmpty()) continue;

            int present = 0;
            int late = 0;
            int absent = 0;

            for (Attendance a: filtered){
                AttendanceStatus status = a.getStatus();
                if(status == AttendanceStatus.PRESENT) present++;
                else if(status == AttendanceStatus.LATE) late++;
                else if(status == AttendanceStatus.ABSENT) absent++;
            }

            // 위험도 결정
            WarningStatus level = decideRiskLevel(present, late, absent);
            if(level == WarningStatus.NONE){
                continue; // 정상 (작성에서 빠질 에정)
            }

            // 결석 보정
            int adjustAbsent = adjustedAbsentForSort(late, absent);
            int remainLate =remainingLateAfterAdjust(late);

            entries.add(new RiskEntry(student.getName(), present, remainLate, adjustAbsent, level));

        }

        if(entries.isEmpty()){
            sb4.append("- 제적 위험자가 없습니다.");
            return sb4.toString();
        }

        // 2. 정렬
        entries.sort((e1, e2) ->{
            // 1) 제적 > 면담 > 경고
            int cmpLevel = Integer.compare(e1.level.getPriority(), e2.level.getPriority());
            if (cmpLevel != 0) return cmpLevel;

            // 2) 결석(보정) 내림차순
            int cmpAbsent = Integer.compare(e2.absent, e1.absent);
            if (cmpAbsent != 0) return cmpAbsent;

            // 3) 지각(보정 후 남은 것) 내림차순
            int cmpLate = Integer.compare(e2.late, e1.late);
            if (cmpLate != 0) return cmpLate;

            // 4) 닉네임 오름차순
            return e1.name.compareTo(e2.name);
        });

        // 3. 출력
        for (RiskEntry e : entries) {
            sb4.append("- ")
                    .append(e.name)
                    .append(": 결석 ").append(e.absent).append("회, ")
                    .append("지각 ").append(e.late).append("회 ")
                    .append("(").append(e.level.getText()).append(")")
                    .append("\n");
        }

        return sb4.toString();
    }

    // RiskEntry DTO
    private static class RiskEntry {
        String name;
        int present;
        int late;           // 보정 후 남은 지각
        int absent;         // 보정 후 결석
        WarningStatus level;

        RiskEntry(String name, int present, int late, int absent, WarningStatus level) {
            this.name = name;
            this.present = present;
            this.late = late;
            this.absent = absent;
            this.level = level;
        }
    }

    private WarningStatus decideRiskLevel(int present, int late, int absent) {
        // 지각 → 결석 보정
        int bonusAbsent = late / 3;
        int adjustedAbsent = absent + bonusAbsent;

        if (adjustedAbsent >= 5) return WarningStatus.DISMISS;
        if (adjustedAbsent >= 3) return WarningStatus.MEETING;
        if (adjustedAbsent >= 2) return WarningStatus.WARNING;
        return WarningStatus.NONE;
    }

    private int adjustedAbsentForSort(int late, int absent) {
        return absent + (late / 3);
    }

    private int remainingLateAfterAdjust(int late) {
        return late % 3;
    }


}
