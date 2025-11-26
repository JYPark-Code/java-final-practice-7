package repository;

import entity.Student;

import java.util.Collection;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> findByName(String name);
    Collection<Student> findAll();

    void save(Student student);
}
