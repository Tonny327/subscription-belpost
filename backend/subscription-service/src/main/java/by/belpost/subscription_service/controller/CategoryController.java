package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.CategoryDto;
import by.belpost.subscription_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/top")
    public List<CategoryDto> getTopCategories() {
        return categoryService.getTopLevelCategories();
    }

    @GetMapping("/{parentId}/children")
    public List<CategoryDto> getChildren(@PathVariable Long parentId) {
        return categoryService.getSubCategories(parentId);
    }
}

