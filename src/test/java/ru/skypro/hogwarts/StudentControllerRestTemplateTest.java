package ru.skypro.hogwarts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.skypro.hogwarts.controller.StudentController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(studentController).isNotNull();
    }

    @Test
    void testCreateStudent() {
        Student student = new Student(0, "Harry", 17);
        ResponseEntity<Student> response = restTemplate.postForEntity(getBaseUrl() + "/student", student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testCreateAndGetStudent() {
        Student student = new Student(0, "Harry", 17);
        ResponseEntity<Student> postResponse = restTemplate.postForEntity(getBaseUrl() + "/student", student, Student.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student created = postResponse.getBody();

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Harry");

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(getBaseUrl() + "/student/" + created.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getResponse.getBody()).getAge()).isEqualTo(17);
    }

    @Test
    void testEditStudent() {
        Student student = new Student(0, "Hermione", 16);
        student = restTemplate.postForObject(getBaseUrl() + "/student", student, Student.class);

        student.setAge(17);
        HttpEntity<Student> request = new HttpEntity<>(student);
        ResponseEntity<Student> putResponse = restTemplate.exchange(getBaseUrl() + "/student/" + student.getId(), HttpMethod.PUT, request, Student.class);

        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(putResponse.getBody()).getAge()).isEqualTo(17);
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student(0, "Ron", 16);
        student = restTemplate.postForObject(getBaseUrl() + "/student", student, Student.class);

        restTemplate.delete(getBaseUrl() + "/student/" + student.getId());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(getBaseUrl() + "/student/" + student.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindStudentsByAgeRange() {
        restTemplate.postForObject(getBaseUrl() + "/student", new Student(0, "Dean", 15), Student.class);
        restTemplate.postForObject(getBaseUrl() + "/student", new Student(0, "Seamus", 17), Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/student?min=15&max=17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentFaculty() {
        Faculty faculty = new Faculty(0, "Gryffindor", "Red");
        faculty = restTemplate.postForObject(getBaseUrl() + "/faculty", faculty, Faculty.class);

        Student student = new Student(0, "Neville", 17);
        student.setFaculty(faculty);
        student = restTemplate.postForObject(getBaseUrl() + "/student", student, Student.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/student/" + student.getId() + "/faculty", Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo("Gryffindor");
    }
}
