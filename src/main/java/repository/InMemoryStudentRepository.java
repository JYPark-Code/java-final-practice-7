package repository;

import entity.Student;

import java.util.*;

public class InMemoryStudentRepository implements StudentRepository {

    private final Map<String, Student> students = new HashMap<>();

    @Override
    public Optional<Student> findByName(String name) {
        return Optional.ofNullable(students.get(name));
    }

    @Override
    public Collection<Student> findAll() {
        return students.values();
    }

    @Override
    public void save(Student student) {
        students.put(student.getName(), student);
    }

}
