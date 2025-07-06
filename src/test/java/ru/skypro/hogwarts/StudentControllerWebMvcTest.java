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

    private static final String BASE_PATH = "/student";
    private static final String STUDENT_ID_PATH = "/1";

    private static final String STUDENT_NAME = "Harry";
    private static final String STUDENT_SECOND_NAME = "Hermione";
    private static final String STUDENT_THIRD_NAME = "Ron";

    private static final int STUDENT_AGE = 17;
    private static final int STUDENT_EDITED_AGE = 16;

    private static final String FACULTY_NAME = "Gryffindor";
    private static final String FACULTY_COLOUR = "Red";

    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_NAME = "$.name";
    private static final String JSON_PATH_AGE = "$.age";
    private static final String JSON_PATH_COLOUR = "$.colour";
    private static final String JSON_PATH_FIRST_NAME = "$[0].name";

    @Test
    void testGetStudentInfo() throws Exception {
        Student student = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get(BASE_PATH + "/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NAME).value(STUDENT_NAME))
                .andExpect(jsonPath(JSON_PATH_AGE).value(STUDENT_AGE));
    }

    @Test
    void testFindStudentsByAgeRange() throws Exception {
        Student student = new Student(2L, STUDENT_SECOND_NAME, STUDENT_EDITED_AGE);
        Mockito.when(studentService.findByAgeBetween(15, 18)).thenReturn(List.of(student));

        mockMvc.perform(get(BASE_PATH)
                        .param("min", "15")
                        .param("max", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(STUDENT_SECOND_NAME));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, FACULTY_NAME, FACULTY_COLOUR);
        Student student = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get(BASE_PATH + STUDENT_ID_PATH + "/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NAME).value(FACULTY_NAME));
    }

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(0L, STUDENT_THIRD_NAME, STUDENT_EDITED_AGE);
        Student created = new Student(1L, STUDENT_THIRD_NAME, STUDENT_EDITED_AGE);

        Mockito.when(studentService.addStudent(Mockito.any())).thenReturn(created);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1))
                .andExpect(jsonPath(JSON_PATH_NAME).value(STUDENT_THIRD_NAME));
    }

    @Test
    void testEditStudent() throws Exception {
        Student updated = new Student(1L, STUDENT_NAME, STUDENT_AGE);
        Mockito.when(studentService.editStudent(1L, updated)).thenReturn(updated);

        mockMvc.perform(put(BASE_PATH + STUDENT_ID_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NAME).value(STUDENT_NAME));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete(BASE_PATH + STUDENT_ID_PATH))
                .andExpect(status().isOk());
    }
}