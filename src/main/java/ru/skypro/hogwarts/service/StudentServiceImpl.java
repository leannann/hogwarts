package ru.skypro.hogwarts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.repositories.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student");
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no student with id = {}", id);
                    return new RuntimeException("Student not found");
                });
    }

    public Student editStudent(long id, Student student) {
        logger.info("Was invoked method for edit student");
        if (!studentRepository.existsById(id)) {
            logger.warn("Attempt to edit non-existing student with id = {}", id);
            return null;
        }
        student.setId(id);
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.warn("Attempting to delete student with id = {}", id);
        studentRepository.deleteById(id);
        logger.debug("Student with id = {} has been deleted", id);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for finding students between ages {} and {}", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public long getStudentCount() {
        logger.info("Was invoked method for counting students");
        return studentRepository.getStudentCount();
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for calculating average student age");
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for getting last 5 students");
        return studentRepository.findLast5Students();
    }

    public List<String> getNamesStartingWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && name.toUpperCase().startsWith("A"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
    }

    public double getAverageStudentAge() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0); // если список пуст — вернуть 0.0
    }

}
