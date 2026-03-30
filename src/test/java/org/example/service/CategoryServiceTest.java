package org.example.service;

import org.example.dto.CategoryRequestDto;
import org.example.dto.CategoryResponseDto;
import org.example.entity.Category;
import org.example.exception.CategoryNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void createCategory_ShouldReturnCreatedCategory_WithNoParent() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", null);
        Category saved = new Category();
        saved.setCategoryId(1L);
        saved.setName("Electronics");
        saved.setParentCategory(null);
        saved.setSubCategories(Collections.emptyList());

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDto result = categoryService.createCategory(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getName());
        assertNull(result.getParentCategoryId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory_WithParent() {
        Category parent = new Category();
        parent.setCategoryId(1L);
        parent.setName("Electronics");
        parent.setParentCategory(null);
        parent.setSubCategories(Collections.emptyList());

        CategoryRequestDto requestDto = new CategoryRequestDto("Mobile", 1L);
        Category saved = new Category();
        saved.setCategoryId(2L);
        saved.setName("Mobile");
        saved.setParentCategory(parent);
        saved.setSubCategories(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDto result = categoryService.createCategory(requestDto);

        assertNotNull(result);
        assertEquals(2L, result.getCategoryId());
        assertEquals("Mobile", result.getName());
        assertEquals(1L, result.getParentCategoryId());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenParentNotFound() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Mobile", 99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.createCategory(requestDto));

        assertEquals("Category not found with id: 99", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
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
    void getCategoryById_ShouldReturnCategory() {
        electronicsCategory.setSubCategories(Collections.emptyList());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));

        CategoryResponseDto result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.getCategoryById(1L));

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Consumer Electronics", null);
        Category updated = new Category();
        updated.setCategoryId(1L);
        updated.setName("Consumer Electronics");
        updated.setParentCategory(null);
        updated.setSubCategories(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryResponseDto result = categoryService.updateCategory(1L, requestDto);

        assertNotNull(result);
        assertEquals("Consumer Electronics", result.getName());
        assertNull(result.getParentCategoryId());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Electronics", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.updateCategory(1L, requestDto));

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteSuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () ->
                categoryService.deleteCategory(1L));

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, never()).deleteById(1L);
    }
}
