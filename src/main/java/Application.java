import command.Command;
import entity.Student;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;
import util.CsvLoader;

import java.io.IOException;
import java.util.Scanner;

public class Application {



    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        StudentRepository repo = new InMemoryStudentRepository();

        // 2) 테스트용 크루 몇 명 미리 등록
//        repo.save(new Student("이든"));
//        repo.save(new Student("제리"));
//        repo.save(new Student("코리"));
//        repo.save(new Student("빙티"));
//        repo.save(new Student("쿠키"));
//        repo.save(new Student("빙봉"));

        // CSV에서 초기 데이터 로딩
        CsvLoader.load("src/main/resources/attendance.csv", repo);

        // 3) Command 생성할 때 repo 주입
        Command cmd = new Command(repo);

        try{
            run(sc, cmd);
        } finally {
            sc.close();
        }
    }

    private static void run(Scanner sc, Command cmd) {
        BasicPrompt.printMenu();

        while (true){
            String command = sc.nextLine();

            // 종료
            if(command.strip().equalsIgnoreCase("q")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            switch(command){

                /**
                 * 1. 출석체크
                 *닉네임을 입력해 주세요.
                 *이든
                 *등교 시간을 입력해 주세요.
                 *09:59
                 */
                case "1":
                    System.out.println("닉네임을 입력해 주세요.");
                    String name_1 = sc.nextLine();
                    System.out.println("등교 시간을 입력해 주세요.");
                    String time_1 = sc.nextLine();

                    // c1_attendance가 "12월 13일 금요일 09:59 (출석)" 문자열을 리턴
                    try {
                        String result = cmd.c1_attendance(name_1, time_1);
                        System.out.println(result);   // 한 줄 출력
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                    break;
                /**
                 *  2. 수정
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
                case "2":
                    System.out.println("출석 수정하려는 닉네임을 입력해 주세요.");
                    String name_2 = sc.nextLine();

                    System.out.println("수정하려는 날짜(일)를 입력해 주세요.");
                    int targetdate_2 = sc.nextInt();
                    sc.nextLine(); // ← 버퍼에 남은 줄바꿈(\n) 소비

                    System.out.println("언제로 변경하겠습니까?(등교 시간)");
                    String time_2 = sc.nextLine();

                    // "12월 13일 금요일 09:59 (출석)"
                    try {
                        String result = cmd.c2_edit(name_2, targetdate_2, time_2);
                        System.out.println(result);   // 한 줄 출력
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                    break;
                /**
                 *  3. 출결 기록 보기
                 *
                 * 닉네임을 입력해 주세요.
                 * 빙티
                 *
                 * 이번 달 빙티의 출석 기록입니다.
                 *
                 * 12월 02일 월요일 13:00 (출석)
                 * 12월 03일 화요일 09:58 (출석)
                 * 12월 04일 수요일 10:02 (출석)
                 * 12월 05일 목요일 10:06 (지각)
                 * 12월 06일 금요일 10:01 (출석)
                 * 12월 09일 월요일 --:-- (결석)
                 * 12월 10일 화요일 10:08 (지각)
                 * 12월 11일 수요일 --:-- (결석)
                 * 12월 12일 목요일 --:-- (결석)
                 *
                 * 출석: 4회
                 * 지각: 2회
                 * 결석: 3회
                 *
                 * 면담 대상자입니다.
                 */
                case "3":
                    System.out.println("닉네임을 입력해 주세요.");
                    String name_3 = sc.nextLine();
                    try {
                        String result = cmd.c3_report(name_3);
                        System.out.println(result);   // 한 줄 출력
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }

                    break;
                /**
                 * 4. 제적 위험 리스트 (전체)
                 *
                 * 제적 위험자 조회 결과 - (예시)
                 * - 빙티: 결석 3회, 지각 2회 (면담)
                 * - 이든: 결석 2회, 지각 4회 (면담)
                 * - 쿠키: 결석 2회, 지각 2회 (경고)
                 * - 빙봉: 결석 1회, 지각 5회 (경고)
                 */
                case "4":
                    try {
                    String result = cmd.c4_watchlist();
                    System.out.println();
                    System.out.println(result);
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
            }
            BasicPrompt.printMenu();
        }


    }

}
