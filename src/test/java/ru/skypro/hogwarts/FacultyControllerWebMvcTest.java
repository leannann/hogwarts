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

    private static final String NAME = "Hufflepuff";
    private static final String COLOUR = "Yellow";

    @Test
    void testGetFacultyInfo() throws Exception {
        Faculty faculty = new Faculty(1L, NAME, COLOUR);
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/id").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.colour").value(COLOUR));
    }

    @Test
    void testFindFaculties() throws Exception {
        Faculty faculty = new Faculty(1L, NAME, COLOUR);
        Mockito.when(facultyService.findByNameOrColourIgnoreCase(NAME, COLOUR))
                .thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty")
                        .param("name", NAME)
                        .param("colour", COLOUR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(NAME))
                .andExpect(jsonPath("$[0].colour").value(COLOUR));
    }

    @Test
    void testCreateFaculty() throws Exception {
        Faculty input = new Faculty(0L, NAME, COLOUR);
        Faculty created = new Faculty(1L, NAME, COLOUR);
        Mockito.when(facultyService.addFaculty(Mockito.any())).thenReturn(created);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty updated = new Faculty(1L, NAME, "Silver");
        Mockito.when(facultyService.editFaculty(1L, updated)).thenReturn(updated);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.colour").value("Silver"));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }
}
