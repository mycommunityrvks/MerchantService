package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CategoryRequestDto;
import org.example.dto.CategoryResponseDto;
import org.example.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private CategoryRequestDto requestDto;
    private CategoryResponseDto mobileSubCategoryDto;
    private CategoryResponseDto electronicsCategoryDto;

    @BeforeEach
    void setUp() {
        requestDto = new CategoryRequestDto("Electronics", null);
        mobileSubCategoryDto = new CategoryResponseDto(2L, "Mobile", 1L, Collections.emptyList());
        electronicsCategoryDto = new CategoryResponseDto(1L, "Electronics", null, Arrays.asList(mobileSubCategoryDto));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(any(CategoryRequestDto.class))).thenReturn(electronicsCategoryDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.parentCategoryId").doesNotExist());

        verify(categoryService, times(1)).createCategory(any(CategoryRequestDto.class));
    }

    @Test
    void createCategory_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        CategoryRequestDto invalidRequest = new CategoryRequestDto("", null);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(CategoryRequestDto.class));
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

    @Test
    void getCategoryById_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(electronicsCategoryDto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        when(categoryService.getCategoryById(1L)).thenThrow(new RuntimeException("Category not found with id: 1"));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        CategoryResponseDto updatedDto = new CategoryResponseDto(1L, "Consumer Electronics", null, Collections.emptyList());
        CategoryRequestDto updateRequest = new CategoryRequestDto("Consumer Electronics", null);
        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.name").value("Consumer Electronics"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequestDto.class));
    }

    @Test
    void updateCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDto.class)))
                .thenThrow(new RuntimeException("Category not found with id: 1"));

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequestDto.class));
    }

    @Test
    void updateCategory_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        CategoryRequestDto invalidRequest = new CategoryRequestDto("", null);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).updateCategory(any(Long.class), any(CategoryRequestDto.class));
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void deleteCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        doThrow(new RuntimeException("Category not found with id: 1")).when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
