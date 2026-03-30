package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CategoryResponseDto;
import org.example.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@WithMockUser(username = "admin", password = "password", roles = "USER")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryResponseDto mobileSubCategoryDto;
    private CategoryResponseDto electronicsCategoryDto;

    @BeforeEach
    void setUp() {
        mobileSubCategoryDto = new CategoryResponseDto(2L, "Mobile", 1L, Collections.emptyList());
        electronicsCategoryDto = new CategoryResponseDto(1L, "Electronics", null, Arrays.asList(mobileSubCategoryDto));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        List<CategoryResponseDto> categories = Arrays.asList(electronicsCategoryDto);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].parentCategoryId").doesNotExist())
                .andExpect(jsonPath("$[0].subCategories[0].categoryId").value(2L))
                .andExpect(jsonPath("$[0].subCategories[0].name").value("Mobile"))
                .andExpect(jsonPath("$[0].subCategories[0].parentCategoryId").value(1L));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(categoryService, times(1)).getAllCategories();
    }
}
