package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.LoginResponse;
import by.belpost.subscription_service.dto.UserDto;
import by.belpost.subscription_service.dto.UserLoginRequest;
import by.belpost.subscription_service.dto.UserRegisterRequest;
import by.belpost.subscription_service.dto.UserWithSubscriptionsDto;
import by.belpost.subscription_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/{id}")
    public UserWithSubscriptionsDto getUser(@PathVariable Long id) {
        return userService.getUserWithSubscriptions(id);
    }
}

