package ru.skypro.hogwarts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.repositories.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyServiceImpl implements FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for find faculty");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no faculty with id = {}", id);
                    return new RuntimeException("Faculty not found");
                });
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        logger.info("Was invoked method for edit faculty");
        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempt to edit non-existing faculty with id = {}", id);
            return null;
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty with id = {}", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByNameOrColourIgnoreCase(String name, String colour) {
        logger.info("Was invoked method for search faculty by name or colour");
        return facultyRepository.findByNameOrColourIgnoreCase(name, colour);
    }
}
