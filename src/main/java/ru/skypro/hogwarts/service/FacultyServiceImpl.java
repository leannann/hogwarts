package ru.skypro.hogwarts.service;

import org.springframework.stereotype.Service;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.repositories.FacultyRepository;
import ru.skypro.hogwarts.repositories.StudentRepository;

import java.util.Collection;
import java.util.HashMap;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }


    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).get();
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        if (!facultyRepository.existsById(id)) {
            return null;
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }
    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByNameOrColourIgnoreCase(String name, String colour){
        return facultyRepository.findByNameOrColourIgnoreCase(name, colour);
    }
}
