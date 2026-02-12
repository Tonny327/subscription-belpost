package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.PublicationDto;
import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.entity.Publication;
import by.belpost.subscription_service.enums.PublicationType;
import by.belpost.subscription_service.repository.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private PublicationService publicationService;

    @Test
    void getAllPublications_noFilters_usesFindAll() {
        when(publicationRepository.findAll()).thenReturn(List.of());

        List<PublicationDto> result = publicationService.getAllPublications(null, null);

        assertThat(result).isEmpty();
        verify(publicationRepository).findAll();
    }

    @Test
    void getAllPublications_onlyType_usesFindByType() {
        Publication pub = basePublication();
        when(publicationRepository.findByType(PublicationType.JOURNAL)).thenReturn(List.of(pub));

        List<PublicationDto> result = publicationService.getAllPublications("journal", null);

        assertThat(result).hasSize(1);
        verify(publicationRepository).findByType(PublicationType.JOURNAL);
    }

    @Test
    void getAllPublications_onlyCategory_usesFindByCategoriesId() {
        when(publicationRepository.findByCategoriesId(10L)).thenReturn(List.of());

        publicationService.getAllPublications(null, 10L);

        verify(publicationRepository).findByCategoriesId(10L);
    }

    @Test
    void getAllPublications_typeAndCategory_usesFindByTypeAndCategoriesId() {
        when(publicationRepository.findByTypeAndCategoriesId(any(), any())).thenReturn(List.of());

        publicationService.getAllPublications("NEWSPAPER", 5L);

        verify(publicationRepository).findByTypeAndCategoriesId(PublicationType.NEWSPAPER, 5L);
    }

    @Test
    void getAllPublications_invalidType_throws() {
        assertThatThrownBy(() -> publicationService.getAllPublications("UNKNOWN", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid publication type");
    }

    @Test
    void toDto_mapsAllFieldsAndCategoryNames() {
        Category cat1 = Category.builder().id(1L).name("Наука").build();
        Category cat2 = Category.builder().id(2L).name("Кулинария").build();

        Publication publication = Publication.builder()
                .id(3L)
                .title("Тест")
                .description("Описание")
                .price(9.99)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .imageUrl("http://image")
                .categories(Set.of(cat1, cat2))
                .build();

        PublicationDto dto = publicationService.toDto(publication);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getTitle()).isEqualTo("Тест");
        assertThat(dto.getDescription()).isEqualTo("Описание");
        assertThat(dto.getPrice()).isEqualTo(9.99);
        assertThat(dto.getPeriod()).isEqualTo("1 месяц");
        assertThat(dto.getType()).isEqualTo("JOURNAL");
        assertThat(dto.getImageUrl()).isEqualTo("http://image");
        assertThat(dto.getCategoryNames()).containsExactlyInAnyOrder("Наука", "Кулинария");
    }

    private Publication basePublication() {
        return Publication.builder()
                .id(1L)
                .title("Base")
                .price(10.0)
                .period("1 месяц")
                .type(PublicationType.JOURNAL)
                .build();
    }
}

