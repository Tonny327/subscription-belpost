package by.belpost.subscription_service.service;

import by.belpost.subscription_service.dto.CategoryDto;
import by.belpost.subscription_service.entity.Category;
import by.belpost.subscription_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getTopLevelCategories() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CategoryDto toDto(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(parentId)
                .build();
    }
}

