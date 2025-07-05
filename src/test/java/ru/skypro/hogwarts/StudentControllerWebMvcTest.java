package ru.skypro.hogwarts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skypro.hogwarts.controller.StudentController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.service.StudentService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(StudentControllerWebMvcTest.MockConfig.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public StudentService studentService() {
            return Mockito.mock(StudentService.class);
        }
    }

    @Test
    void testGetStudentInfo() throws Exception {
        Student student = new Student(1L, "Harry", 17);
        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void testFindStudentsByAgeRange() throws Exception {
        Mockito.when(studentService.findByAgeBetween(15, 18))
                .thenReturn(List.of(new Student(1L, "Hermione", 17)));

        mockMvc.perform(get("/student?min=15&max=18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hermione"));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        Student student = new Student(1L, "Harry", 17);
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(0L, "Ron", 16);
        Student created = new Student(1L, "Ron", 16);

        Mockito.when(studentService.addStudent(Mockito.any())).thenReturn(created);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ron"));
    }

    @Test
    void testEditStudent() throws Exception {
        Student student = new Student(1L, "Harry", 17);

        Mockito.when(studentService.editStudent(1L, student)).thenReturn(student);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }
}