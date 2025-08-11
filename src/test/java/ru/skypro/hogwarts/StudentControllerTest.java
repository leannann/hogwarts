package ru.skypro.hogwarts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPrintParallel() throws Exception {
        mockMvc.perform(get("/students/print-parallel"))
                .andExpect(status().isOk());
    }

    @Test
    void testPrintSynchronized() throws Exception {
        mockMvc.perform(get("/students/print-synchronized"))
                .andExpect(status().isOk());
    }
}
