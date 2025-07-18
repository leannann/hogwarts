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

    private static final String STUDENT_PATH = "/student";
    private static final String FACULTY_PATH = "/faculty";

    private static final String STUDENT_NAME_HARRY = "Harry";
    private static final String STUDENT_NAME_HERMIONE = "Hermione";
    private static final String STUDENT_NAME_RON = "Ron";
    private static final String STUDENT_NAME_NEVILLE = "Neville";
    private static final String STUDENT_NAME_DEAN = "Dean";
    private static final String STUDENT_NAME_SEAMUS = "Seamus";

    private static final int AGE_15 = 15;
    private static final int AGE_16 = 16;
    private static final int AGE_17 = 17;

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
        Student student = new Student(0, STUDENT_NAME_HARRY, AGE_17);
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl + STUDENT_PATH, student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(STUDENT_NAME_HARRY);
    }

    @Test
    void testGetStudentById() {
        Student student = createStudent(STUDENT_NAME_HERMIONE, AGE_16);
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + STUDENT_PATH + "/" + student.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo(STUDENT_NAME_HERMIONE);
    }

    @Test
    void testEditStudent() {
        Student student = createStudent(STUDENT_NAME_RON, AGE_15);
        student.setAge(AGE_16);
        HttpEntity<Student> request = new HttpEntity<>(student);

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + STUDENT_PATH + "/" + student.getId(), HttpMethod.PUT, request, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getAge()).isEqualTo(AGE_16);
    }

    @Test
    void testDeleteStudent() {
        Student student = createStudent(STUDENT_NAME_NEVILLE, AGE_17);
        restTemplate.delete(baseUrl + STUDENT_PATH + "/" + student.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + STUDENT_PATH + "/" + student.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindStudentsByAgeRange() {
        createStudent(STUDENT_NAME_DEAN, AGE_15);
        createStudent(STUDENT_NAME_SEAMUS, AGE_17);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + STUDENT_PATH + "?min=15&max=17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentFaculty() {
        Faculty faculty = createFaculty(FACULTY_NAME, FACULTY_COLOR);

        Student student = new Student(0, STUDENT_NAME_HARRY, AGE_17);
        student.setFaculty(faculty);

        student = restTemplate.postForObject(baseUrl + STUDENT_PATH, student, Student.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + STUDENT_PATH + "/" + student.getId() + FACULTY_PATH, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo(FACULTY_NAME);
    }

    private Student createStudent(String name, int age) {
        Student student = new Student(0, name, age);
        return restTemplate.postForObject(baseUrl + STUDENT_PATH, student, Student.class);
    }

    private Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty(0, name, color);
        return restTemplate.postForObject(baseUrl + FACULTY_PATH, faculty, Faculty.class);
    }
}