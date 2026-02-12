package by.belpost.subscription_service.repository;

import by.belpost.subscription_service.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByParentIsNull_returnsOnlyRootCategories() {
        Category root1 = Category.builder().name("Root1").build();
        Category root2 = Category.builder().name("Root2").build();

        Category child1 = Category.builder().name("Child1").parent(root1).build();
        Category child2 = Category.builder().name("Child2").parent(root1).build();

        root1.getChildren().add(child1);
        root1.getChildren().add(child2);

        categoryRepository.save(root1);
        categoryRepository.save(root2);

        List<Category> roots = categoryRepository.findByParentIsNull();

        assertThat(roots).extracting(Category::getName)
                .containsExactlyInAnyOrder("Root1", "Root2");
    }

    @Test
    void findByParentId_returnsChildren() {
        Category root = Category.builder().name("Root").build();
        Category child1 = Category.builder().name("Child1").parent(root).build();
        Category child2 = Category.builder().name("Child2").parent(root).build();
        root.getChildren().add(child1);
        root.getChildren().add(child2);

        Category savedRoot = categoryRepository.save(root);

        List<Category> children = categoryRepository.findByParentId(savedRoot.getId());

        assertThat(children).hasSize(2);
        assertThat(children).extracting(Category::getName)
                .containsExactlyInAnyOrder("Child1", "Child2");
    }
}

