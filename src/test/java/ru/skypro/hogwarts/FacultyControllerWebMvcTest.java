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
import ru.skypro.hogwarts.controller.FacultyController;
import ru.skypro.hogwarts.entities.Faculty;
import ru.skypro.hogwarts.service.FacultyService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
@Import(FacultyControllerWebMvcTest.MockConfig.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public FacultyService facultyService() {
            return Mockito.mock(FacultyService.class);
        }
    }

    @Test
    void testGetFacultyInfo() throws Exception {
        Faculty faculty = new Faculty(1L, "Ravenclaw", "Blue");
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravenclaw"));
    }

    @Test
    void testFindFaculties() throws Exception {
        Mockito.when(facultyService.findByNameOrColourIgnoreCase("Hufflepuff", "Yellow"))
                .thenReturn(List.of(new Faculty(1L, "Hufflepuff", "Yellow")));

        mockMvc.perform(get("/faculty?name=Hufflepuff&colour=Yellow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hufflepuff"));
    }

    @Test
    void testCreateFaculty() throws Exception {
        Faculty input = new Faculty(0L, "Durmstrang", "Dark");
        Faculty created = new Faculty(1L, "Durmstrang", "Dark");

        Mockito.when(facultyService.addFaculty(Mockito.any())).thenReturn(created);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Durmstrang"));
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Beauxbatons", "Silver");

        Mockito.when(facultyService.editFaculty(1L, faculty)).thenReturn(faculty);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Beauxbatons"));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }
}
