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
        BasicPrompt basicPrompt = new BasicPrompt();
        BasicPrompt.printMenu();
    }

}
