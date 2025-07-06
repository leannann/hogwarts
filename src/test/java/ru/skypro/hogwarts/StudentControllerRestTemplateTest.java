package ru.skypro.hogwarts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.hogwarts.controller.StudentController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StudentControllerRestTemplateTest {

    private static final String STUDENT_NAME = "Harry";
    private static final int STUDENT_AGE = 17;
    private static final String FACULTY_NAME = "Gryffindor";
    private static final String FACULTY_COLOR = "Red";

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    void testCreateStudent() {
        Student student = new Student(0, STUDENT_NAME, STUDENT_AGE);
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl + "/student", student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(STUDENT_NAME);
    }

    @Test
    void testGetStudentById() {
        Student student = createStudent("Hermione", 16);
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/student/" + student.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo("Hermione");
    }

    @Test
    void testEditStudent() {
        Student student = createStudent("Ron", 15);
        student.setAge(16);
        HttpEntity<Student> request = new HttpEntity<>(student);

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/student/" + student.getId(), HttpMethod.PUT, request, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getAge()).isEqualTo(16);
    }

    @Test
    void testDeleteStudent() {
        Student student = createStudent("Neville", 17);
        restTemplate.delete(baseUrl + "/student/" + student.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/student/" + student.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindStudentsByAgeRange() {
        createStudent("Dean", 15);
        createStudent("Seamus", 17);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/student?min=15&max=17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentFaculty() {
        Faculty faculty = createFaculty(FACULTY_NAME, FACULTY_COLOR);

        Student student = new Student(0, STUDENT_NAME, STUDENT_AGE);
        student.setFaculty(faculty);

        student = restTemplate.postForObject(baseUrl + "/student", student, Student.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/student/" + student.getId() + "/faculty", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo(FACULTY_NAME);
    }

    private Student createStudent(String name, int age) {
        Student student = new Student(0, name, age);
        return restTemplate.postForObject(baseUrl + "/student", student, Student.class);
    }

    private Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty(0, name, color);
        return restTemplate.postForObject(baseUrl + "/faculty", faculty, Faculty.class);
    }
}