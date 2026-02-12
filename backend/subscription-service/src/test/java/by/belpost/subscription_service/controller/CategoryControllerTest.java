package by.belpost.subscription_service.controller;

import by.belpost.subscription_service.dto.CategoryDto;
import by.belpost.subscription_service.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void getTopCategories_returnsList() throws Exception {
        List<CategoryDto> dtos = List.of(
                CategoryDto.builder().id(1L).name("Взрослому").parentId(null).build(),
                CategoryDto.builder().id(2L).name("Ребенку").parentId(null).build()
        );
        when(categoryService.getTopLevelCategories()).thenReturn(dtos);

        mockMvc.perform(get("/api/categories/top"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Взрослому")))
                .andExpect(jsonPath("$[0].parentId").doesNotExist());
    }

    @Test
    void getChildren_returnsChildrenForParent() throws Exception {
        List<CategoryDto> dtos = List.of(
                CategoryDto.builder().id(3L).name("Хобби").parentId(1L).build()
        );
        when(categoryService.getSubCategories(1L)).thenReturn(dtos);

        mockMvc.perform(get("/api/categories/1/children"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].parentId", is(1)));
    }
}

