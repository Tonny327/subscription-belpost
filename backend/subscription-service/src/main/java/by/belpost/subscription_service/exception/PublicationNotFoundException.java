package by.belpost.subscription_service.exception;

public class PublicationNotFoundException extends RuntimeException {

    public PublicationNotFoundException(Long id) {
        super("Publication not found with id " + id);
    }
}

