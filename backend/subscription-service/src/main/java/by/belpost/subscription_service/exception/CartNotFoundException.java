package by.belpost.subscription_service.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(Long id) {
        super("Cart not found with id " + id);
    }

    public CartNotFoundException(String cartToken) {
        super("Cart not found with token " + cartToken);
    }
}

