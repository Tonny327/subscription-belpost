package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.AddCartItemRequest;
import by.belpost.subscription_service.dto.CartDto;
import by.belpost.subscription_service.dto.CartItemDto;
import by.belpost.subscription_service.dto.CreateOrGetCartRequest;
import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @Test
    void createOrGetCart_returnsCart() throws Exception {
        CartDto cartDto = CartDto.builder()
                .id(10L)
                .items(List.of())
                .totalPrice(0.0)
                .build();

        when(cartService.createOrGetCart(any(CreateOrGetCartRequest.class))).thenReturn(cartDto);

        CreateOrGetCartRequest request = CreateOrGetCartRequest.builder()
                .cartToken("token-123")
                .build();

        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    void addItem_returnsUpdatedCart() throws Exception {
        CartItemDto itemDto = CartItemDto.builder()
                .id(1L)
                .publication(PublicationDto.builder()
                        .id(5L)
                        .title("Журнал")
                        .type("JOURNAL")
                        .categoryNames(Set.of("Наука"))
                        .build())
                .period("1 месяц")
                .quantity(1)
                .totalPrice(10.0)
                .build();

        CartDto cartDto = CartDto.builder()
                .id(10L)
                .items(List.of(itemDto))
                .totalPrice(10.0)
                .build();

        when(cartService.addItem(any(AddCartItemRequest.class))).thenReturn(cartDto);

        AddCartItemRequest request = AddCartItemRequest.builder()
                .publicationId(5L)
                .period("1 месяц")
                .quantity(1)
                .cartId(10L)
                .build();

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].publication.id", is(5)))
                .andExpect(jsonPath("$.items[0].totalPrice", is(10.0)));
    }

    @Test
    void deleteItem_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart/items/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCartById_returnsCart() throws Exception {
        CartDto cartDto = CartDto.builder()
                .id(10L)
                .items(List.of())
                .totalPrice(0.0)
                .build();

        when(cartService.getCartById(10L)).thenReturn(cartDto);

        mockMvc.perform(get("/api/cart/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    void getCartByToken_returnsCart() throws Exception {
        CartDto cartDto = CartDto.builder()
                .id(10L)
                .items(List.of())
                .totalPrice(0.0)
                .build();

        when(cartService.getCartByToken("token-123")).thenReturn(cartDto);

        mockMvc.perform(get("/api/cart/by-token/token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }
}

