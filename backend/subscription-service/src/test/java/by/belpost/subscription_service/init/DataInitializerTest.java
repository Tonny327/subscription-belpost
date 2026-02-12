package by.belpost.subscription_service.init;

import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.repository.CategoryRepository;
import by.belpost.subscription_service.repository.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataInitializerTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    void dataInitializer_populatesCategoriesAndPublications() {
        assertThat(categoryRepository.findAll())
                .extracting("name")
                .contains("Взрослому", "Ребенку");

        long publicationsCount = publicationRepository.count();
        assertThat(publicationsCount).isGreaterThanOrEqualTo(11);

        assertThat(publicationRepository.findAll())
                .extracting("type")
                .contains(PublicationType.JOURNAL, PublicationType.NEWSPAPER);
    }
}

