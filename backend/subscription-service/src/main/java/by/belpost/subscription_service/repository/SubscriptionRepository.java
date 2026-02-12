package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}

