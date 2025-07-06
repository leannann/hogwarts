package ru.skypro.hogwarts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    private static final String BASE_PATH = "/student";
    private static final String FACULTY_PATH = "/faculty";
    private static final String BASE_URL_FORMAT = "http://localhost:%d";

    private static final String NAME_HARRY = "Harry";
    private static final String NAME_HERMIONE = "Hermione";
    private static final String NAME_RON = "Ron";
    private static final String NAME_DEAN = "Dean";
    private static final String NAME_SEAMUS = "Seamus";
    private static final String NAME_NEVILLE = "Neville";
    private static final String FACULTY_NAME = "Gryffindor";
    private static final String FACULTY_COLOR = "Red";

    private static final int AGE_15 = 15;
    private static final int AGE_16 = 16;
    private static final int AGE_17 = 17;

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return String.format(BASE_URL_FORMAT, port);
    }

    private Student buildStudent(String name, int age) {
        return new Student(0, name, age);
    }

    @Test
    void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    void testCreateStudent() {
        Student student = buildStudent(NAME_HARRY, AGE_17);
        ResponseEntity<Student> response = restTemplate.postForEntity(getBaseUrl() + BASE_PATH, student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(NAME_HARRY);
    }

    @Test
    void testCreateAndGetStudent() {
        Student student = buildStudent(NAME_HARRY, AGE_17);
        Student created = restTemplate.postForObject(getBaseUrl() + BASE_PATH, student, Student.class);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(NAME_HARRY);

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(getBaseUrl() + BASE_PATH + "/" + created.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getResponse.getBody()).getAge()).isEqualTo(AGE_17);
    }

    @Test
    void testEditStudent() {
        Student student = buildStudent(NAME_HERMIONE, AGE_16);
        student = restTemplate.postForObject(getBaseUrl() + BASE_PATH, student, Student.class);

        student.setAge(AGE_17);
        HttpEntity<Student> request = new HttpEntity<>(student);
        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl() + BASE_PATH + "/" + student.getId(), HttpMethod.PUT, request, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getAge()).isEqualTo(AGE_17);
    }

    @Test
    void testDeleteStudent() {
        Student student = buildStudent(NAME_RON, AGE_16);
        student = restTemplate.postForObject(getBaseUrl() + BASE_PATH, student, Student.class);

        restTemplate.delete(getBaseUrl() + BASE_PATH + "/" + student.getId());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(getBaseUrl() + BASE_PATH + "/" + student.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindStudentsByAgeRange() {
        restTemplate.postForObject(getBaseUrl() + BASE_PATH, buildStudent(NAME_DEAN, AGE_15), Student.class);
        restTemplate.postForObject(getBaseUrl() + BASE_PATH, buildStudent(NAME_SEAMUS, AGE_17), Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + BASE_PATH + "?min=15&max=17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentFaculty() {
        Faculty faculty = new Faculty(0, FACULTY_NAME, FACULTY_COLOR);
        faculty = restTemplate.postForObject(getBaseUrl() + FACULTY_PATH, faculty, Faculty.class);

        Student student = buildStudent(NAME_NEVILLE, AGE_17);
        student.setFaculty(faculty);
        student = restTemplate.postForObject(getBaseUrl() + BASE_PATH, student, Student.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + BASE_PATH + "/" + student.getId() + FACULTY_PATH, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo(FACULTY_NAME);
    }
}