package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.UserProfileDto;
import by.belpost.subscription_service.entity.User;
import by.belpost.subscription_service.exception.UserNotFoundException;
import by.belpost.subscription_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserProfile_returnsDto() {
        User user = User.builder()
                .id(1L)
                .fullName("Имя")
                .email("test@example.com")
                .phone("+375291234567")
                .passwordHash("hash")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileDto dto = userService.getUserProfile(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFullName()).isEqualTo("Имя");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getPhone()).isEqualTo("+375291234567");
    }

    @Test
    void getUserProfile_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserProfile_updatesFields() {
        Instant created = Instant.now().minusSeconds(3600);
        Instant updated = Instant.now().minusSeconds(1800);

        User user = User.builder()
                .id(1L)
                .fullName("Старое имя")
                .email("old@example.com")
                .phone("+375291111111")
                .passwordHash("hash")
                .createdAt(created)
                .updatedAt(updated)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileDto request = UserProfileDto.builder()
                .id(1L)
                .fullName("Новое имя")
                .email("new@example.com")
                .phone("+375292222222")
                .build();

        UserProfileDto dto = userService.updateUserProfile(1L, request);

        assertThat(dto.getFullName()).isEqualTo("Новое имя");
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
        assertThat(dto.getPhone()).isEqualTo("+375292222222");
        verify(userRepository).save(any(User.class));
    }
}

