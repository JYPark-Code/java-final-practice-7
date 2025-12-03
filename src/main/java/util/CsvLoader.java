package util;

import domain.Attendance;
import domain.Student;
import repository.StudentRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CsvLoader {

    // date : yyyy-MM-dd HH:mm
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static void load(String filepath, StudentRepository repo) throws IOException {
        Path path = Path.of(filepath);

        try(BufferedReader br = Files.newBufferedReader(path)){
            String line = br.readLine();

            while((line = br.readLine()) != null){
                if(line.isBlank()) continue;

                String[] parts = line.split(",", 2);
                if(parts.length < 2) {
                     continue;
                }

                String name = parts[0].trim();
                String dateText = parts[1].trim();

                // 1. 날짜 파싱
                LocalDateTime datetime = LocalDateTime.parse(dateText, DATETIME_FMT);
                LocalDate date = datetime.toLocalDate();
                LocalTime arrivalTime = datetime.toLocalTime();

                // 2. 수업 시작 시간( 월 : 13:00, 나머지 10:00 )
                LocalTime lessonStart = getLessonStart(date);

                // 3. Student 찾기 또는 생성
                Student student = findOrCreateStudent(repo, name);

                // 4. Attendance 생성 (status -> fromArrival에서 계산)
                Attendance attendance = Attendance.fromArrival(date, lessonStart, arrivalTime);

                // 5. 학생 출석 기록 추가
                student.addAttendance(attendance);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 로딩 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

    }

    private static Student findOrCreateStudent(StudentRepository repo, String name) {
        Optional<Student> optional = repo.findByName(name);
        if(optional.isPresent()){
            return optional.get();
        }

        Student s = new Student(name);
        repo.save(s);
        return s;
    }

    private static LocalTime getLessonStart(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.MONDAY){
            return LocalTime.of(13, 0); // 월요일 13시 (오후 1시)
        }
        return LocalTime.of(10, 0); // 화~금 10시
    }

}
