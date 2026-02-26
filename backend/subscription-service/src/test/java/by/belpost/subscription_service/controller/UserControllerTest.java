package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.LoginResponse;
import by.belpost.subscription_service.dto.UserDto;
import by.belpost.subscription_service.dto.UserLoginRequest;
import by.belpost.subscription_service.dto.UserProfileDto;
import by.belpost.subscription_service.dto.UserRegisterRequest;
import by.belpost.subscription_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_returnsUserDto() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .fullName("Иван Иванов")
                .email("ivan@example.com")
                .phone("+375291234567")
                .password("secret123")
                .build();

        UserDto response = UserDto.builder()
                .id(1L)
                .fullName("Иван Иванов")
                .email("ivan@example.com")
                .phone("+375291234567")
                .build();

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иван Иванов")));
    }

    @Test
    void login_returnsLoginResponse() throws Exception {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("ivan@example.com")
                .password("secret123")
                .build();

        LoginResponse response = LoginResponse.builder()
                .user(UserDto.builder()
                        .id(1L)
                        .fullName("Иван Иванов")
                        .email("ivan@example.com")
                        .build())
                .token("token")
                .build();

        when(userService.login(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.token", is("token")));
    }

    @Test
    void getUserProfile_returnsProfile() throws Exception {
        UserProfileDto profile = UserProfileDto.builder()
                .id(1L)
                .fullName("Иван Иванов")
                .phone("+375291234567")
                .email("ivan@example.com")
                .build();

        when(userService.getUserProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иван Иванов")))
                .andExpect(jsonPath("$.email", is("ivan@example.com")));
    }

    @Test
    void updateUserProfile_returnsUpdatedProfile() throws Exception {
        UserProfileDto request = UserProfileDto.builder()
                .id(1L)
                .fullName("Новое Имя")
                .phone("+375291111111")
                .email("new@example.com")
                .build();

        when(userService.updateUserProfile(any(Long.class), any(UserProfileDto.class))).thenReturn(request);

        mockMvc.perform(post("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Новое Имя")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }
}

