package ru.skypro.hogwarts.service;

import ru.skypro.hogwarts.entities.Faculty;

import java.util.Collection;

public interface FacultyService {

    Faculty addFaculty(Faculty faculty);

    Faculty findFaculty(long id);

    Faculty editFaculty(long id, Faculty faculty);

    void deleteFaculty(long id);

    Collection<Faculty> findByNameOrColourIgnoreCase(String name, String colour);
}
