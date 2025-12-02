package command;

import java.util.Scanner;

public class CommandRouter {

    private final Command cmd;
    private final Scanner sc;

    public CommandRouter(Command cmd, Scanner sc){
        this.cmd = cmd;
        this.sc = sc;
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
            System.out.println("닉네임을 입력해 주세요.");
            String name = sc.nextLine();

            System.out.println("등교 시간을 입력해 주세요.");
            String time = sc.nextLine();

            System.out.println(cmd.c1_attendance(name, time));
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void runEdit(){
        try{
            System.out.println("출석 수정하려는 닉네임을 입력해 주세요.");
            String name = sc.nextLine();

            System.out.println("수정하려는 날짜(일)를 입력해 주세요.");
            int day = Integer.parseInt(sc.nextLine());

            System.out.println("언제로 변경하겠습니까?(등교 시간)");
            String time = sc.nextLine();

            System.out.println(cmd.c2_edit(name, day, time));
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void runReport(){
        try{
            System.out.println("닉네임을 입력해 주세요.");
            String name = sc.nextLine();
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

