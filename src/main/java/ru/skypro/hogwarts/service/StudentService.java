package ru.skypro.hogwarts.service;

import ru.skypro.hogwarts.entities.Student;

import java.util.Collection;
import java.util.List;

public interface StudentService {

    Student addStudent(Student student);

    Student findStudent(long id);

    Student editStudent(long id, Student student);

    void deleteStudent(long id);

    Collection<Student> findByAgeBetween(int min, int max);

    long getStudentCount();
    Double getAverageAge();
    List<Student> getLastFiveStudents();

    List<String> getNamesStartingWithA();
    double getAverageStudentAge();

    List<Student> getFirstSixStudents();

}
