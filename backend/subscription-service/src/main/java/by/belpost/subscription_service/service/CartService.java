package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.AddCartItemRequest;
import by.belpost.subscription_service.dto.CartDto;
import by.belpost.subscription_service.dto.CartItemDto;
import by.belpost.subscription_service.dto.CreateOrGetCartRequest;
import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.entity.Cart;
import by.belpost.subscription_service.entity.CartItem;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.entity.User;
import by.belpost.subscription_service.exception.CartItemNotFoundException;
import by.belpost.subscription_service.exception.CartNotFoundException;
import by.belpost.subscription_service.exception.InvalidCartItemQuantityException;
import by.belpost.subscription_service.exception.InvalidSubscriptionPeriodException;
import by.belpost.subscription_service.exception.PublicationNotFoundException;
import by.belpost.subscription_service.exception.UserNotFoundException;
import by.belpost.subscription_service.repository.CartItemRepository;
import by.belpost.subscription_service.repository.CartRepository;
import by.belpost.subscription_service.repository.PublicationRepository;
import by.belpost.subscription_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final PublicationService publicationService;

    public CartDto createOrGetCart(CreateOrGetCartRequest request) {
        Cart cart = findExistingCart(request.getUserId(), request.getCartToken())
                .orElseGet(() -> createCart(request.getUserId(), request.getCartToken()));
        return toDto(cart);
    }

    public CartDto addItem(AddCartItemRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidCartItemQuantityException(
                    request.getQuantity() != null ? request.getQuantity() : 0
            );
        }

        Cart cart = resolveCartForItem(request);

        Publication publication = publicationRepository.findById(request.getPublicationId())
                .orElseThrow(() -> new PublicationNotFoundException(request.getPublicationId()));

        // Validate period and calculate price (discount logic same as SubscriptionService)
        int months = resolveMonths(request.getPeriod());
        double discountMultiplier = discountMultiplier(months);
        double totalPrice = publication.getPrice() * months * discountMultiplier * request.getQuantity();

        CartItem item = CartItem.builder()
                .cart(cart)
                .publication(publication)
                .period(request.getPeriod())
                .quantity(request.getQuantity())
                .build();

        cart.getItems().add(item);
        cart.setUpdatedAt(Instant.now());

        Cart saved = cartRepository.save(cart);
        return toDto(saved);
    }

    public void removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    public CartDto getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
        return toDto(cart);
    }

    public CartDto getCartByToken(String cartToken) {
        Cart cart = cartRepository.findByCartToken(cartToken)
                .orElseThrow(() -> new CartNotFoundException(cartToken));
        return toDto(cart);
    }

    private java.util.Optional<Cart> findExistingCart(Long userId, String cartToken) {
        if (userId != null) {
            return cartRepository.findByUserId(userId);
        }
        if (cartToken != null && !cartToken.isBlank()) {
            return cartRepository.findByCartToken(cartToken);
        }
        return java.util.Optional.empty();
    }

    private Cart createCart(Long userId, String providedToken) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        }
        Instant now = Instant.now();
        String token = providedToken != null && !providedToken.isBlank()
                ? providedToken
                : UUID.randomUUID().toString();

        Cart cart = Cart.builder()
                .user(user)
                .cartToken(token)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return cartRepository.save(cart);
    }

    private Cart resolveCartForItem(AddCartItemRequest request) {
        if (request.getCartId() != null) {
            return cartRepository.findById(request.getCartId())
                    .orElseThrow(() -> new CartNotFoundException(request.getCartId()));
        }
        if (request.getCartToken() != null && !request.getCartToken().isBlank()) {
            return cartRepository.findByCartToken(request.getCartToken())
                    .orElseThrow(() -> new CartNotFoundException(request.getCartToken()));
        }
        if (request.getUserId() != null) {
            return cartRepository.findByUserId(request.getUserId())
                    .orElseGet(() -> createCart(request.getUserId(), null));
        }
        // No identifiers -> create anonymous cart
        return createCart(null, null);
    }

    private CartDto toDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        double total = itemDtos.stream()
                .mapToDouble(CartItemDto::getTotalPrice)
                .sum();

        return CartDto.builder()
                .id(cart.getId())
                .items(itemDtos)
                .totalPrice(total)
                .build();
    }

    private CartItemDto toItemDto(CartItem item) {
        Publication publication = item.getPublication();
        PublicationDto publicationDto = publicationService.toDto(publication);

        int months = resolveMonths(item.getPeriod());
        double discountMultiplier = discountMultiplier(months);
        double totalPrice = publication.getPrice() * months * discountMultiplier * item.getQuantity();

        return CartItemDto.builder()
                .id(item.getId())
                .publication(publicationDto)
                .period(item.getPeriod())
                .quantity(item.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }

    private int resolveMonths(String period) {
        String normalized = period.trim().toLowerCase();
        return switch (normalized) {
            case "1 месяц", "1 мес", "месяц" -> 1;
            case "3 месяца", "3 мес" -> 3;
            case "6 месяцев", "6 мес" -> 6;
            case "1 год", "год" -> 12;
            default -> throw new InvalidSubscriptionPeriodException(period);
        };
    }

    private double discountMultiplier(int months) {
        return switch (months) {
            case 1 -> 1.0;
            case 3 -> 0.97;
            case 6 -> 0.95;
            case 12 -> 0.90;
            default -> 1.0;
        };
    }
}

