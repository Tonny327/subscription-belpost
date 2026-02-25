package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.AddCartItemRequest;
import by.belpost.subscription_service.dto.CartDto;
import by.belpost.subscription_service.dto.CreateOrGetCartRequest;
import by.belpost.subscription_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartDto createOrGetCart(@RequestBody CreateOrGetCartRequest request) {
        return cartService.createOrGetCart(request);
    }

    @PostMapping("/items")
    public CartDto addItem(@Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public CartDto getCart(@PathVariable Long id) {
        return cartService.getCartById(id);
    }

    @GetMapping("/by-token/{cartToken}")
    public CartDto getCartByToken(@PathVariable String cartToken) {
        return cartService.getCartByToken(cartToken);
    }
}

