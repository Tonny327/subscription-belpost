package by.belpost.subscription_service.exception;

public class InvalidCartItemQuantityException extends RuntimeException {

    public InvalidCartItemQuantityException(int quantity) {
        super("Invalid cart item quantity: " + quantity);
    }
}

