package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.dto.SubscriptionRequestDto;
import by.belpost.subscription_service.dto.SubscriptionResponseDto;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.entity.Subscription;
import by.belpost.subscription_service.enums.SubscriptionStatus;
import by.belpost.subscription_service.repository.PublicationRepository;
import by.belpost.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PublicationRepository publicationRepository;

    public SubscriptionResponseDto createSubscription(SubscriptionRequestDto request) {
        Publication publication = publicationRepository.findById(request.getPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found with id " + request.getPublicationId()));

        int months = resolveMonths(request.getPeriod());

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = startDate.plusMonths(months);
        Double totalPrice = publication.getPrice() * months;

        Subscription subscription = Subscription.builder()
                .publication(publication)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .startDate(startDate)
                .endDate(endDate)
                .period(request.getPeriod())
                .totalPrice(totalPrice)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return toResponseDto(saved);
    }

    public SubscriptionResponseDto getById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id " + id));
        return toResponseDto(subscription);
    }

    private int resolveMonths(String period) {
        String normalized = period.trim().toLowerCase();
        return switch (normalized) {
            case "1 месяц", "1 мес", "месяц" -> 1;
            case "3 месяца", "3 мес" -> 3;
            case "6 месяцев", "6 мес" -> 6;
            case "1 год", "год" -> 12;
            default -> throw new RuntimeException("Unsupported subscription period: " + period);
        };
    }

    private SubscriptionResponseDto toResponseDto(Subscription subscription) {
        Publication publication = subscription.getPublication();

        Set<String> categoryNames = publication.getCategories()
                .stream()
                .map(cat -> cat.getName())
                .collect(Collectors.toSet());

        PublicationDto publicationDto = PublicationDto.builder()
                .id(publication.getId())
                .title(publication.getTitle())
                .description(publication.getDescription())
                .price(publication.getPrice())
                .period(publication.getPeriod())
                .type(publication.getType() != null ? publication.getType().name() : null)
                .imageUrl(publication.getImageUrl())
                .categoryNames(categoryNames)
                .build();

        return SubscriptionResponseDto.builder()
                .id(subscription.getId())
                .publication(publicationDto)
                .customerName(subscription.getCustomerName())
                .customerPhone(subscription.getCustomerPhone())
                .customerEmail(subscription.getCustomerEmail())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .period(subscription.getPeriod())
                .totalPrice(subscription.getTotalPrice())
                .build();
    }
}

