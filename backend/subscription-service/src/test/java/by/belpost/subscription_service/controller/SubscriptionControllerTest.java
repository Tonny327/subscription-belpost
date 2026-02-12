package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.dto.SubscriptionRequestDto;
import by.belpost.subscription_service.dto.SubscriptionResponseDto;
import by.belpost.subscription_service.enums.SubscriptionStatus;
import by.belpost.subscription_service.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    void createSubscription_validRequest_returnsResponse() throws Exception {
        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .publicationId(1L)
                .customerName("Иван Петров")
                .customerPhone("+375291234567")
                .customerEmail("ivan@example.com")
                .startDate(LocalDate.of(2026, 2, 12))
                .period("3 месяца")
                .build();

        SubscriptionResponseDto response = SubscriptionResponseDto.builder()
                .id(10L)
                .publication(PublicationDto.builder()
                        .id(1L)
                        .title("Журнал")
                        .type("JOURNAL")
                        .categoryNames(Set.of("Наука"))
                        .build())
                .customerName("Иван Петров")
                .customerPhone("+375291234567")
                .customerEmail("ivan@example.com")
                .startDate(LocalDate.of(2026, 2, 12))
                .endDate(LocalDate.of(2026, 5, 12))
                .period("3 месяца")
                .totalPrice(30.0)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionService.createSubscription(any(SubscriptionRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.customerName", is("Иван Петров")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void createSubscription_invalidRequest_returnsBadRequest() throws Exception {
        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .publicationId(null)
                .customerName("")
                .customerPhone("")
                .customerEmail("invalid-email")
                .startDate(null)
                .period("")
                .build();

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSubscription_returnsResponse() throws Exception {
        SubscriptionResponseDto response = SubscriptionResponseDto.builder()
                .id(5L)
                .customerName("Иван Петров")
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionService.getById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/subscriptions/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.customerName", is("Иван Петров")));
    }
}

