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
import ru.skypro.hogwarts.repositories.FacultyRepository;
import ru.skypro.hogwarts.repositories.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    private static final String BASE_NAME = "Hufflepuff";
    private static final String BASE_COLOUR = "Yellow";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(BASE_NAME);
        faculty.setColour(BASE_COLOUR);

        ResponseEntity<Faculty> response = restTemplate.postForEntity(getBaseUrl() + "/faculty", faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(BASE_NAME);
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = facultyRepository.save(new Faculty(0, BASE_NAME, BASE_COLOUR));

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + faculty.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getColour()).isEqualTo(BASE_COLOUR);
    }

    @Test
    void testEditFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(0, BASE_NAME, BASE_COLOUR));
        faculty.setColour("Silver");

        HttpEntity<Faculty> request = new HttpEntity<>(faculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(getBaseUrl() + "/faculty/" + faculty.getId(), HttpMethod.PUT, request, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getColour()).isEqualTo("Silver");
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(0, BASE_NAME, BASE_COLOUR));

        restTemplate.delete(getBaseUrl() + "/faculty/" + faculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + faculty.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindByNameOrColourIgnoreCase() {
        facultyRepository.saveAll(List.of(
                new Faculty(0, "SilverHouse", "Silver"),
                new Faculty(0, "GoldHouse", "Gold")
        ));

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/faculty?name=silver&colour=gold",
                Faculty[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testGetStudentsByFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(0, "BlueHouse", "Blue"));
        Student student = new Student(0, "TestStudent", 18);
        student.setFaculty(faculty);
        studentRepository.save(student);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/faculty/" + faculty.getId() + "/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1);
        assertThat(response.getBody()[0].getName()).isEqualTo("TestStudent");
    }
}
