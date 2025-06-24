package ru.skypro.hogwarts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long>{

    Collection<Faculty> findByNameOrColourIgnoreCase(String name, String colour);
}
