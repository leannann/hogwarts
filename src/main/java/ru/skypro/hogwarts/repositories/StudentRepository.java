package ru.skypro.hogwarts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.hogwarts.entities.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAgeBetween(int min, int max);
}
