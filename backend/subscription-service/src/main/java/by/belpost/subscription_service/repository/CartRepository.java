package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCartToken(String cartToken);

    Optional<Cart> findByUserId(Long userId);
}

