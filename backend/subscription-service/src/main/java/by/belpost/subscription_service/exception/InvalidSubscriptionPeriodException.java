package by.belpost.subscription_service.exception;

public class InvalidSubscriptionPeriodException extends RuntimeException {

    public InvalidSubscriptionPeriodException(String period) {
        super("Unsupported subscription period: " + period);
    }
}

