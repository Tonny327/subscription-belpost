package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.service.PublicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicationController.class)
class PublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicationService publicationService;

    @Test
    void getPublications_withoutParams_returnsAll() throws Exception {
        List<PublicationDto> dtos = List.of(
                PublicationDto.builder()
                        .id(1L)
                        .title("Журнал 1")
                        .type("JOURNAL")
                        .categoryNames(Set.of("Наука"))
                        .build()
        );

        when(publicationService.getAllPublications(null, null)).thenReturn(dtos);

        mockMvc.perform(get("/api/publications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Журнал 1")))
                .andExpect(jsonPath("$[0].type", is("JOURNAL")));
    }

    @Test
    void getPublications_withTypeAndCategory_passesParamsToService() throws Exception {
        when(publicationService.getAllPublications(eq("JOURNAL"), eq(10L))).thenReturn(List.of());

        mockMvc.perform(get("/api/publications")
                        .param("type", "JOURNAL")
                        .param("categoryId", "10"))
                .andExpect(status().isOk());
    }
}

