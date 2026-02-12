package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.CategoryDto;
import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getTopLevelCategories_mapsToDto() {
        Category root = Category.builder()
                .id(1L)
                .name("Взрослому")
                .build();

        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(root));

        List<CategoryDto> result = categoryService.getTopLevelCategories();

        assertThat(result).hasSize(1);
        CategoryDto dto = result.getFirst();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Взрослому");
        assertThat(dto.getParentId()).isNull();
    }

    @Test
    void getSubCategories_returnsChildrenWithParentId() {
        Category parent = Category.builder()
                .id(1L)
                .name("Взрослому")
                .build();

        Category child = Category.builder()
                .id(2L)
                .name("Хобби")
                .parent(parent)
                .build();

        when(categoryRepository.findByParentId(1L)).thenReturn(List.of(child));

        List<CategoryDto> result = categoryService.getSubCategories(1L);

        assertThat(result).hasSize(1);
        CategoryDto dto = result.getFirst();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Хобби");
        assertThat(dto.getParentId()).isEqualTo(1L);
    }
}

