package attendance;

import camp.nextstep.edu.missionutils.DateTimes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BasicPrompt {

    public static void printMenu(){

        LocalDate today = DateTimes.now().toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN);

        System.out.println("오늘은 " + today.format(formatter) + "입니다. 기능을 선택해 주세요.");
        System.out.println("1. 출석 확인");
        System.out.println("2. 출석 수정");
        System.out.println("3. 크루별 출석 기록 확인");
        System.out.println("4. 제적 위험자 확인");
        System.out.println("Q. 종료");

    }


}
