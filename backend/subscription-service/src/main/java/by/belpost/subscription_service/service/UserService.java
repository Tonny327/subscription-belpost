package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.LoginResponse;
import by.belpost.subscription_service.dto.SubscriptionResponseDto;
import by.belpost.subscription_service.dto.UserDto;
import by.belpost.subscription_service.dto.UserLoginRequest;
import by.belpost.subscription_service.dto.UserProfileDto;
import by.belpost.subscription_service.dto.UserRegisterRequest;
import by.belpost.subscription_service.dto.UserWithSubscriptionsDto;
import by.belpost.subscription_service.entity.Subscription;
import by.belpost.subscription_service.entity.User;
import by.belpost.subscription_service.exception.InvalidCredentialsException;
import by.belpost.subscription_service.exception.UserAlreadyExistsException;
import by.belpost.subscription_service.exception.UserNotFoundException;
import by.belpost.subscription_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    public UserDto register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        Instant now = Instant.now();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(hashPassword(request.getPassword()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public LoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (!passwordMatches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = UUID.randomUUID().toString();
        return LoginResponse.builder()
                .user(toDto(user))
                .token(token)
                .build();
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return toProfileDto(user);
    }

    public UserProfileDto updateUserProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        return toProfileDto(saved);
    }

    public UserWithSubscriptionsDto getUserWithSubscriptions(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<SubscriptionResponseDto> subscriptionDtos = user.getSubscriptions()
                .stream()
                .map(this::mapSubscription)
                .collect(Collectors.toList());

        return UserWithSubscriptionsDto.builder()
                .user(toDto(user))
                .subscriptions(subscriptionDtos)
                .build();
    }

    private UserProfileDto toProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    private SubscriptionResponseDto mapSubscription(Subscription subscription) {
        return subscriptionService.getById(subscription.getId());
    }

    private String hashPassword(String rawPassword) {
        // Simple placeholder hash for demo purposes
        return Integer.toHexString(rawPassword.hashCode());
    }

    private boolean passwordMatches(String rawPassword, String hashed) {
        return hashPassword(rawPassword).equals(hashed);
    }
}

