package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PublicationRepositoryTest {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByType_returnsOnlyGivenType() {
        Publication journal = basePublication("Journal", PublicationType.JOURNAL);
        Publication newspaper = basePublication("Newspaper", PublicationType.NEWSPAPER);

        publicationRepository.save(journal);
        publicationRepository.save(newspaper);

        List<Publication> journals = publicationRepository.findByType(PublicationType.JOURNAL);

        assertThat(journals).hasSize(1);
        assertThat(journals.getFirst().getType()).isEqualTo(PublicationType.JOURNAL);
    }

    @Test
    void findByCategoriesId_returnsByCategory() {
        Category science = Category.builder().name("Наука").build();
        Category savedCategory = categoryRepository.save(science);

        Publication p1 = basePublication("P1", PublicationType.JOURNAL);
        p1.setCategories(Set.of(savedCategory));

        Publication p2 = basePublication("P2", PublicationType.JOURNAL);

        publicationRepository.save(p1);
        publicationRepository.save(p2);

        List<Publication> result = publicationRepository.findByCategoriesId(savedCategory.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("P1");
    }

    @Test
    void findByTypeAndCategoriesId_returnsIntersection() {
        Category science = Category.builder().name("Наука").build();
        Category savedCategory = categoryRepository.save(science);

        Publication p1 = basePublication("P1", PublicationType.JOURNAL);
        p1.setCategories(Set.of(savedCategory));

        Publication p2 = basePublication("P2", PublicationType.NEWSPAPER);
        p2.setCategories(Set.of(savedCategory));

        publicationRepository.save(p1);
        publicationRepository.save(p2);

        List<Publication> result =
                publicationRepository.findByTypeAndCategoriesId(PublicationType.JOURNAL, savedCategory.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("P1");
    }

    private Publication basePublication(String title, PublicationType type) {
        return Publication.builder()
                .title(title)
                .description("desc")
                .price(10.0)
                .period("1 месяц")
                .type(type)
                .build();
    }
}

