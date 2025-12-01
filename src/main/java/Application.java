import command.Command;
import entity.Student;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;

import java.util.Scanner;

public class Application {



    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        StudentRepository repo = new InMemoryStudentRepository();

        // 2) 테스트용 크루 몇 명 미리 등록
        repo.save(new Student("이든"));
        repo.save(new Student("제리"));
        repo.save(new Student("코리"));

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
                    String name = sc.nextLine();
                    System.out.println("등교 시간을 입력해 주세요.");
                    String time = sc.nextLine();

                    // c1_attendance가 "12월 13일 금요일 09:59 (출석)" 같은 문자열을 리턴한다고 가정
                    try {
                        String result = cmd.c1_attendance(name, time);
                        System.out.println(result);   // 한 줄 출력
                    } catch (IllegalArgumentException e) {
                        System.out.println("[ERROR] " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("2번 기능");
                    break;
                case "3":
                    System.out.println("3번 기능");
                    break;
                case "4":
                    System.out.println("4번 기능");
                    break;
                case "5":
                    System.out.println("5번 기능");
                    break;
                default:
                    throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
            }
            BasicPrompt.printMenu();
        }


    }

}
