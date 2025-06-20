package ru.skypro.hogwarts.service;

import ru.skypro.hogwarts.entities.Student;

public interface StudentService {

    Student addStudent(Student student);

    Student findStudent(long id);

    Student editStudent(long id, Student student);

    void deleteStudent(long id);
}
