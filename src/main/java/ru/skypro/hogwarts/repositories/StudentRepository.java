package ru.skypro.hogwarts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.hogwarts.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
