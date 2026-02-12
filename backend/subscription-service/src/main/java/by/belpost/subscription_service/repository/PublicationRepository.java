package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByType(PublicationType type);

    List<Publication> findByCategoriesId(Long categoryId);

    List<Publication> findByTypeAndCategoriesId(PublicationType type, Long categoryId);
}

