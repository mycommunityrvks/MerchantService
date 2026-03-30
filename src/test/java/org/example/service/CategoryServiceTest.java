package org.example.service;

import org.example.dto.CategoryResponseDto;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category electronicsCategory;
    private Category mobileSubCategory;

    @BeforeEach
    void setUp() {
        electronicsCategory = new Category();
        electronicsCategory.setCategoryId(1L);
        electronicsCategory.setName("Electronics");
        electronicsCategory.setParentCategory(null);

        mobileSubCategory = new Category();
        mobileSubCategory.setCategoryId(2L);
        mobileSubCategory.setName("Mobile");
        mobileSubCategory.setParentCategory(electronicsCategory);
        mobileSubCategory.setSubCategories(Collections.emptyList());

        electronicsCategory.setSubCategories(Arrays.asList(mobileSubCategory));
    }

    @Test
    void getAllCategories_ShouldReturnTopLevelCategoriesWithSubCategories() {
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(Arrays.asList(electronicsCategory));

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());

        CategoryResponseDto dto = result.get(0);
        assertEquals(1L, dto.getCategoryId());
        assertEquals("Electronics", dto.getName());
        assertNull(dto.getParentCategoryId());

        assertEquals(1, dto.getSubCategories().size());
        CategoryResponseDto subDto = dto.getSubCategories().get(0);
        assertEquals(2L, subDto.getCategoryId());
        assertEquals("Mobile", subDto.getName());
        assertEquals(1L, subDto.getParentCategoryId());

        verify(categoryRepository, times(1)).findByParentCategoryIsNull();
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(Collections.emptyList());

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByParentCategoryIsNull();
    }

    @Test
    void getAllCategories_ShouldReturnCategoryWithNoSubCategories() {
        electronicsCategory.setSubCategories(Collections.emptyList());
        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(Arrays.asList(electronicsCategory));

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertTrue(result.get(0).getSubCategories().isEmpty());

        verify(categoryRepository, times(1)).findByParentCategoryIsNull();
    }
}
