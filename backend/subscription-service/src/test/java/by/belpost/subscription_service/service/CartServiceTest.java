package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.AddCartItemRequest;
import by.belpost.subscription_service.dto.CartDto;
import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.entity.Cart;
import by.belpost.subscription_service.entity.CartItem;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.exception.InvalidCartItemQuantityException;
import by.belpost.subscription_service.exception.InvalidSubscriptionPeriodException;
import by.belpost.subscription_service.exception.PublicationNotFoundException;
import by.belpost.subscription_service.repository.CartItemRepository;
import by.belpost.subscription_service.repository.CartRepository;
import by.belpost.subscription_service.repository.PublicationRepository;
import by.belpost.subscription_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PublicationService publicationService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItem_createsCartAndCalculatesTotalPriceWithDiscount() {
        Publication publication = Publication.builder()
                .id(1L)
                .title("Журнал")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        Cart emptyCart = Cart.builder()
                .id(5L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart c = invocation.getArgument(0);
            if (c.getId() == null) {
                c.setId(5L);
            }
            return c;
        });

        when(publicationService.toDto(publication)).thenReturn(
                PublicationDto.builder()
                        .id(1L)
                        .title("Журнал")
                        .type("JOURNAL")
                        .categoryNames(Set.of("Наука"))
                        .build()
        );

        AddCartItemRequest request = AddCartItemRequest.builder()
                .publicationId(1L)
                .period("3 месяца")
                .quantity(2)
                .build();

        CartDto dto = cartService.addItem(request);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getPublication().getId()).isEqualTo(1L);
        // 10 * 3 * 0.97 * 2
        assertThat(dto.getItems().get(0).getTotalPrice()).isCloseTo(58.2, within(0.0001));
        assertThat(dto.getTotalPrice()).isCloseTo(58.2, within(0.0001));
    }

    @Test
    void addItem_invalidQuantity_throws() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .publicationId(1L)
                .period("1 месяц")
                .quantity(0)
                .build();

        assertThatThrownBy(() -> cartService.addItem(request))
                .isInstanceOf(InvalidCartItemQuantityException.class);
    }

    @Test
    void addItem_unknownPublication_throws() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        AddCartItemRequest request = AddCartItemRequest.builder()
                .publicationId(1L)
                .period("1 месяц")
                .quantity(1)
                .build();

        assertThatThrownBy(() -> cartService.addItem(request))
                .isInstanceOf(PublicationNotFoundException.class);
    }

    @Test
    void addItem_invalidPeriod_throws() {
        Publication publication = Publication.builder()
                .id(1L)
                .title("Журнал")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        AddCartItemRequest request = AddCartItemRequest.builder()
                .publicationId(1L)
                .period("2 месяца")
                .quantity(1)
                .build();

        assertThatThrownBy(() -> cartService.addItem(request))
                .isInstanceOf(InvalidSubscriptionPeriodException.class);
    }
}

