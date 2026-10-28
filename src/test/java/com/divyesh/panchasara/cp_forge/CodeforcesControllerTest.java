package com.divyesh.panchasara.cp_forge;

import com.divyesh.panchasara.cp_forge.dto.CodeforcesAddRequest;
import com.divyesh.panchasara.cp_forge.dto.CodeforcesProfileDto;
import com.divyesh.panchasara.cp_forge.service.CodeforcesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CodeforcesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CodeforcesService codeforcesService;

    @Test
    void listProfilesReturnsEmptyList() throws Exception {
        Mockito.when(codeforcesService.listProfiles()).thenReturn(List.of());
        mockMvc.perform(get("/api/codeforces/profiles"))
                .andExpect(status().isOk());
    }

    @Test
    void addProfileValidationFailsForInvalidHandle() throws Exception {
        CodeforcesAddRequest request = new CodeforcesAddRequest("bad handle!");
        mockMvc.perform(post("/api/codeforces/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addProfileReturnsOk() throws Exception {
        CodeforcesProfileDto dto = new CodeforcesProfileDto(
                1L, "tourist", 3800, 4000, "legendary grandmaster", "legendary grandmaster",
                null, null, null, OffsetDateTime.now(), List.of()
        );
        Mockito.when(codeforcesService.addOrRefreshProfile(anyString())).thenReturn(dto);

        CodeforcesAddRequest request = new CodeforcesAddRequest("tourist");
        mockMvc.perform(post("/api/codeforces/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handle").value("tourist"));
    }
}
