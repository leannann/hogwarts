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
import ru.skypro.hogwarts.controller.FacultyController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.service.FacultyService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/faculty";
    private static final String FACULTY_NAME = "Hufflepuff";
    private static final String FACULTY_COLOUR = "Yellow";
    private static final String UPDATED_COLOUR = "Silver";
    private static final String JSON_PATH_NAME = "$.name";
    private static final String JSON_PATH_COLOUR = "$.colour";
    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_ARRAY_NAME = "$[0].name";
    private static final String JSON_ARRAY_COLOUR = "$[0].colour";

    @Test
    void testGetFacultyInfo() throws Exception {
        Faculty faculty = new Faculty(1L, FACULTY_NAME, FACULTY_COLOUR);
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get(BASE_URL + "/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_NAME).value(FACULTY_NAME))
                .andExpect(jsonPath(JSON_PATH_COLOUR).value(FACULTY_COLOUR));
    }

    @Test
    void testFindFaculties() throws Exception {
        Faculty faculty = new Faculty(1L, FACULTY_NAME, FACULTY_COLOUR);
        Mockito.when(facultyService.findByNameOrColourIgnoreCase(FACULTY_NAME, FACULTY_COLOUR))
                .thenReturn(List.of(faculty));

        mockMvc.perform(get(BASE_URL)
                        .param("name", FACULTY_NAME)
                        .param("colour", FACULTY_COLOUR))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_ARRAY_NAME).value(FACULTY_NAME))
                .andExpect(jsonPath(JSON_ARRAY_COLOUR).value(FACULTY_COLOUR));
    }

    @Test
    void testCreateFaculty() throws Exception {
        Faculty input = new Faculty(0L, FACULTY_NAME, FACULTY_COLOUR);
        Faculty created = new Faculty(1L, FACULTY_NAME, FACULTY_COLOUR);
        Mockito.when(facultyService.addFaculty(Mockito.any())).thenReturn(created);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(1))
                .andExpect(jsonPath(JSON_PATH_NAME).value(FACULTY_NAME));
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty updated = new Faculty(1L, FACULTY_NAME, UPDATED_COLOUR);
        Mockito.when(facultyService.editFaculty(1L, updated)).thenReturn(updated);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_COLOUR).value(UPDATED_COLOUR));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }
}