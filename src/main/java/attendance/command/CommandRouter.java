package attendance.command;

import camp.nextstep.edu.missionutils.Console;

public class CommandRouter {

    private final Command cmd;

    public CommandRouter(Command cmd){
        this.cmd = cmd;
    }

    public boolean execute(String input){
        if(input.equalsIgnoreCase("q")) {
            System.out.println("프로그램을 종료합니다.");
            return false;
        }

        if(input.equals("1")) {
            runAttendance();
            return true;
        }

        if(input.equals("2")) {
            runEdit();
            return true;
        }

        if(input.equals("3")) {
            runReport();
            return true;
        }

        if(input.equals("4")) {
            runWatchlist();
            return true;
        }

        System.out.println("[ERROR] 잘못된 명령입니다.");
        return true;
    }

    private void runAttendance(){
        try{
            // 토, 일요일 아닌지 체크
            cmd.validateAttendableDate();

            System.out.println("닉네임을 입력해 주세요.");
            String name = Console.readLine();

            // 닉네임 검증
            cmd.validateNickname(name);

            System.out.println("등교 시간을 입력해 주세요.");
            String time = Console.readLine();

            System.out.println(cmd.c1_attendance(name, time));
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void runEdit(){
        try{
            System.out.println("출석 수정하려는 닉네임을 입력해 주세요.");
            String name = Console.readLine();

            System.out.println("수정하려는 날짜(일)를 입력해 주세요.");
            int day = Integer.parseInt(Console.readLine());

            System.out.println("언제로 변경하겠습니까?(등교 시간)");
            String time = Console.readLine();

            System.out.println(cmd.c2_edit(name, day, time));

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] 날짜는 숫자로 입력해 주세요.");

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void runReport(){
        try{
            System.out.println("닉네임을 입력해 주세요.");
            String name = Console.readLine();
            System.out.println(cmd.c3_report(name));
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void runWatchlist(){
        try{
            System.out.println(cmd.c4_watchlist());
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
}

