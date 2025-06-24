package ru.skypro.hogwarts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.hogwarts.entities.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long>{
}
