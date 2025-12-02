import command.Command;
import command.CommandRouter;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;
import util.CsvLoader;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        StudentRepository repo = new InMemoryStudentRepository();
        CsvLoader.load("src/main/resources/attendance.csv", repo);

        Command cmd = new Command(repo);
        CommandRouter router = new CommandRouter(cmd, sc);

        BasicPrompt.printMenu();

        while (true){
            String input = sc.nextLine();
            boolean continueProgram = router.execute(input);
            if(!continueProgram) break;
            BasicPrompt.printMenu();
        }

        sc.close();
    }
}

