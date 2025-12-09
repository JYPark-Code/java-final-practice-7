import camp.nextstep.edu.missionutils.Console;
import command.Command;
import command.CommandRouter;
import repository.InMemoryStudentRepository;
import repository.StudentRepository;
import service.AttendanceService;
import service.ReportService;
import service.WatchlistService;
import util.CsvLoader;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {

        StudentRepository repo = new InMemoryStudentRepository();

        AttendanceService attendanceService = new AttendanceService(repo);
        ReportService reportService = new ReportService(repo);
        WatchlistService watchlistService = new WatchlistService(repo);


        CsvLoader.load("src/main/resources/attendance.csv", repo);

        Command cmd = new Command(attendanceService, reportService, watchlistService);
        CommandRouter router = new CommandRouter(cmd);

        BasicPrompt.printMenu();

        while (true){
            String input = Console.readLine();
            boolean continueProgram = router.execute(input);
            if(!continueProgram) break;
            BasicPrompt.printMenu();
        }

        Console.close();
    }
}

