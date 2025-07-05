package ru.skypro.hogwarts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.skypro.hogwarts.controller.FacultyController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void contextLoads() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    void testCreateAndGetFaculty() {
        Faculty faculty = new Faculty(0, "Hufflepuff", "Yellow");
        ResponseEntity<Faculty> postResponse = restTemplate.postForEntity(getBaseUrl() + "/faculty", faculty, Faculty.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Faculty created = postResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Hufflepuff");

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + created.getId(), Faculty.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getResponse.getBody()).getColour()).isEqualTo("Yellow");
    }

    @Test
    void testEditFaculty() {
        Faculty faculty = new Faculty(0, "Ravenclaw", "Blue");
        faculty = restTemplate.postForObject(getBaseUrl() + "/faculty", faculty, Faculty.class);

        faculty.setColour("Silver");
        HttpEntity<Faculty> request = new HttpEntity<>(faculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(getBaseUrl() + "/faculty/" + faculty.getId(), HttpMethod.PUT, request, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getColour()).isEqualTo("Silver");
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty(0, "Durmstrang", "Gray");
        faculty = restTemplate.postForObject(getBaseUrl() + "/faculty", faculty, Faculty.class);

        restTemplate.delete(getBaseUrl() + "/faculty/" + faculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + faculty.getId(), Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindByNameOrColourIgnoreCase() {
        restTemplate.postForObject(getBaseUrl() + "/faculty", new Faculty(0, "Beauxbatons", "Silver"), Faculty.class);
        restTemplate.postForObject(getBaseUrl() + "/faculty", new Faculty(0, "Silverstorm", "White"), Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl() + "/faculty?name=silver&colour=white", Faculty[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testGetStudentsByFaculty() {
        Faculty faculty = new Faculty(0, "TestHouse", "Blue");
        faculty = restTemplate.postForObject(getBaseUrl() + "/faculty", faculty, Faculty.class);

        Student student = new Student(0, "TestStudent", 18);
        student.setFaculty(faculty);
        student = restTemplate.postForObject(getBaseUrl() + "/student", student, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + faculty.getId() + "/students", Student[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
