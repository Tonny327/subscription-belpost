package by.belpost.subscription_service.exception;

public class InvalidPublicationTypeException extends RuntimeException {

    public InvalidPublicationTypeException(String type) {
        super("Invalid publication type: " + type);
    }
}

