package ru.skypro.hogwarts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.service.StudentService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id){
        Student student = studentService.findStudent(id);
        if (student == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam int min, @RequestParam int max){
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null || student.getFaculty() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {return studentService.addStudent(student);}

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student, @PathVariable Long id){
        Student foundStudent = studentService.editStudent(id, student);
        if (foundStudent == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public long getStudentCount() {
        return studentService.getStudentCount();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last5")
    public List<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getNamesStartingWithA() {
        return studentService.getNamesStartingWithA();
    }

    @GetMapping("/average-age-stream")
    public double getAverageAgeUsingStream() {
        return studentService.getAverageStudentAge();
    }

    @GetMapping("/students/print-parallel")
    public ResponseEntity<Void> printStudentsParallel() {
        List<Student> students = studentService.getFirstSixStudents(); // или findAll().subList(...)

        if (students.size() < 6) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Main thread: " + students.get(0).getName());
        System.out.println("Main thread: " + students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1: " + students.get(2).getName());
            System.out.println("Thread 1: " + students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2: " + students.get(4).getName());
            System.out.println("Thread 2: " + students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        return ResponseEntity.ok().build();
    }

    private synchronized void printStudentNames(String threadName, Student s1, Student s2) {
        System.out.println(threadName + ": " + s1.getName());
        System.out.println(threadName + ": " + s2.getName());
    }

    @GetMapping("/students/print-synchronized")
    public ResponseEntity<Void> printStudentsSynchronized() {
        List<Student> students = studentService.getFirstSixStudents();

        if (students.size() < 6) {
            return ResponseEntity.badRequest().build();
        }

        printStudentNames("Main thread", students.get(0), students.get(1));

        Thread thread1 = new Thread(() -> {
            printStudentNames("Thread 1", students.get(2), students.get(3));
        });

        Thread thread2 = new Thread(() -> {
            printStudentNames("Thread 2", students.get(4), students.get(5));
        });

        thread1.start();
        thread2.start();

        return ResponseEntity.ok().build();
    }




}
