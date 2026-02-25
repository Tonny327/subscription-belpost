package by.belpost.subscription_service.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long id) {
        super("Cart item not found with id " + id);
    }
}

