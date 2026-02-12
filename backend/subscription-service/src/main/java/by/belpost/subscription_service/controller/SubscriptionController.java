package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.SubscriptionRequestDto;
import by.belpost.subscription_service.dto.SubscriptionResponseDto;
import by.belpost.subscription_service.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionResponseDto createSubscription(@Valid @RequestBody SubscriptionRequestDto request) {
        return subscriptionService.createSubscription(request);
    }

    @GetMapping("/{id}")
    public SubscriptionResponseDto getSubscription(@PathVariable Long id) {
        return subscriptionService.getById(id);
    }
}

