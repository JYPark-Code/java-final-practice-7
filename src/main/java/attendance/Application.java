package attendance;

import camp.nextstep.edu.missionutils.Console;
import attendance.command.Command;
import attendance.command.CommandRouter;
import attendance.repository.InMemoryStudentRepository;
import attendance.repository.StudentRepository;
import attendance.service.AttendanceService;
import attendance.service.ReportService;
import attendance.service.WatchlistService;
import attendance.util.CsvLoader;

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

