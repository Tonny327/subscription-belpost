package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.entity.Subscription;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.enums.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    void saveAndFindById_works() {
        Publication publication = Publication.builder()
                .title("Журнал")
                .description("desc")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();

        Publication savedPublication = publicationRepository.save(publication);

        Subscription subscription = Subscription.builder()
                .publication(savedPublication)
                .customerName("Имя")
                .customerPhone("+375291234567")
                .customerEmail("test@example.com")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .period("1 месяц")
                .totalPrice(10.0)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        Optional<Subscription> found = subscriptionRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPublication().getId()).isEqualTo(savedPublication.getId());
        assertThat(found.get().getCustomerName()).isEqualTo("Имя");
    }
}

