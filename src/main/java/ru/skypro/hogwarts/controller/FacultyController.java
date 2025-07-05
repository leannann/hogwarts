package ru.skypro.hogwarts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable long id){
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam String name, @RequestParam String colour){
        return ResponseEntity.ok(facultyService.findByNameOrColourIgnoreCase(name, colour));
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsByFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null || faculty.getStudents() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty.getStudents());
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {return facultyService.addFaculty(faculty);}

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@PathVariable Long id, @RequestBody Faculty faculty){
        Faculty foundFaculty = facultyService.editFaculty(id, faculty);
        if (foundFaculty == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id){
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }
}
