package ru.skypro.hogwarts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.hogwarts.controller.StudentController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.entities.Student;
import ru.skypro.hogwarts.service.StudentService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String STUDENT_NAME = "Harry";
    private static final int STUDENT_AGE = 17;
    private static final String FACULTY_NAME = "Gryffindor";
    private static final String FACULTY_COLOUR = "Red";

    @Test
    void testGetStudentInfo() throws Exception {
        Student student = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.age").value(STUDENT_AGE));
    }

    @Test
    void testFindStudentsByAgeRange() throws Exception {
        Student student = new Student(2L, "Hermione", 16);
        Mockito.when(studentService.findByAgeBetween(15, 18)).thenReturn(List.of(student));

        mockMvc.perform(get("/student")
                        .param("min", "15")
                        .param("max", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hermione"));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, FACULTY_NAME, FACULTY_COLOUR);
        Student student = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_NAME));
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ron"));
    }

    @Test
    void testEditStudent() throws Exception {
        Student updated = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        Mockito.when(studentService.editStudent(1L, updated)).thenReturn(updated);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(STUDENT_NAME));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }
}