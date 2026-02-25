package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.SubscriptionRequestDto;
import by.belpost.subscription_service.dto.SubscriptionResponseDto;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.entity.Subscription;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.enums.SubscriptionStatus;
import by.belpost.subscription_service.exception.InvalidSubscriptionPeriodException;
import by.belpost.subscription_service.exception.PublicationNotFoundException;
import by.belpost.subscription_service.exception.SubscriptionNotFoundException;
import by.belpost.subscription_service.repository.PublicationRepository;
import by.belpost.subscription_service.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void createSubscription_calculatesEndDateAndTotalPrice_forDifferentPeriods() {
        Publication publication = Publication.builder()
                .id(1L)
                .title("Тест")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription s = invocation.getArgument(0);
                    s.setId(100L);
                    return s;
                });

        LocalDate start = LocalDate.of(2026, 2, 12);

        assertPeriod(start, "1 месяц", 1, 10.0);
        assertPeriod(start, "3 месяца", 3, 29.1);
        assertPeriod(start, "6 месяцев", 6, 57.0);
        assertPeriod(start, "1 год", 12, 108.0);
        assertPeriod(start, "1 мес", 1, 10.0);
        assertPeriod(start, "год", 12, 108.0);
    }

    @Test
    void createSubscription_unknownPeriod_throws() {
        Publication publication = Publication.builder()
                .id(1L)
                .title("Тест")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .publicationId(1L)
                .customerName("Имя")
                .customerPhone("+375291234567")
                .customerEmail("test@example.com")
                .startDate(LocalDate.now())
                .period("2 месяца")
                .build();

        assertThatThrownBy(() -> subscriptionService.createSubscription(request))
                .isInstanceOf(InvalidSubscriptionPeriodException.class)
                .hasMessageContaining("Unsupported subscription period");
    }

    @Test
    void createSubscription_publicationNotFound_throws() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .publicationId(1L)
                .customerName("Имя")
                .customerPhone("+375291234567")
                .customerEmail("test@example.com")
                .startDate(LocalDate.now())
                .period("1 месяц")
                .build();

        assertThatThrownBy(() -> subscriptionService.createSubscription(request))
                .isInstanceOf(PublicationNotFoundException.class)
                .hasMessageContaining("Publication not found");
    }

    @Test
    void getById_returnsSubscription() {
        Publication publication = Publication.builder()
                .id(1L)
                .title("Тест")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        Subscription subscription = Subscription.builder()
                .id(5L)
                .publication(publication)
                .customerName("Имя")
                .customerPhone("+375291234567")
                .customerEmail("test@example.com")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .period("1 месяц")
                .totalPrice(10.0)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionRepository.findById(5L)).thenReturn(Optional.of(subscription));

        SubscriptionResponseDto response = subscriptionService.getById(5L);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getCustomerName()).isEqualTo("Имя");
        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void getById_notFound_throws() {
        when(subscriptionRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getById(5L))
                .isInstanceOf(SubscriptionNotFoundException.class)
                .hasMessageContaining("Subscription not found");
    }

    private void assertPeriod(LocalDate start, String periodString, int expectedMonths, double expectedTotal) {
        SubscriptionRequestDto request = SubscriptionRequestDto.builder()
                .publicationId(1L)
                .customerName("Имя")
                .customerPhone("+375291234567")
                .customerEmail("test@example.com")
                .startDate(start)
                .period(periodString)
                .build();

        SubscriptionResponseDto response = subscriptionService.createSubscription(request);

        assertThat(response.getStartDate()).isEqualTo(start);
        assertThat(response.getEndDate()).isEqualTo(start.plusMonths(expectedMonths));
        assertThat(response.getTotalPrice()).isCloseTo(expectedTotal, within(0.0001));
        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }
}

