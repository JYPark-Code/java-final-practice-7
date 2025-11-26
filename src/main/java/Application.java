import java.util.Scanner;

public class Application {



    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try{
            run(sc);
        } finally {
            sc.close();
        }
    }

    private static void run(Scanner sc) {
        BasicPrompt.printMenu();

        while (true){
            String command = sc.nextLine();

            // 종료
            if(command.strip().equalsIgnoreCase("q")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            switch(command){
                case "1":
                    System.out.println("1번 기능");
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
