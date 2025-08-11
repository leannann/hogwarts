package ru.skypro.hogwarts.service;

import org.springframework.stereotype.Service;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.repositories.StudentRepository;

import java.util.List;

@Service
public class StudentPrintService {

    private final StudentRepository studentRepository;

    public StudentPrintService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private List<Student> getFirstSixStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.size() < 6) {
            throw new IllegalStateException("Need at least 6 students in database");
        }
        return students.subList(0, 6);
    }

    public void printParallel() {
        List<Student> students = getFirstSixStudents();

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
    }

    private synchronized void printStudentNames(String threadName, Student s1, Student s2) {
        System.out.println(threadName + ": " + s1.getName());
        System.out.println(threadName + ": " + s2.getName());
    }

    public void printSynchronized() {
        List<Student> students = getFirstSixStudents();

        printStudentNames("Main thread", students.get(0), students.get(1));

        Thread thread1 = new Thread(() -> printStudentNames("Thread 1", students.get(2), students.get(3)));
        Thread thread2 = new Thread(() -> printStudentNames("Thread 2", students.get(4), students.get(5)));

        thread1.start();
        thread2.start();
    }
}
