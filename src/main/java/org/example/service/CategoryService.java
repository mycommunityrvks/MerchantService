package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CategoryRequestDto;
import org.example.dto.CategoryResponseDto;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = new Category();
        category.setName(requestDto.getName());
        if (requestDto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + requestDto.getParentCategoryId()));
            category.setParentCategory(parent);
        }
        Category saved = categoryRepository.save(category);
        return mapToResponseDto(saved);
    }

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return mapToResponseDto(category);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setName(requestDto.getName());
        if (requestDto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + requestDto.getParentCategoryId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }
        Category updated = categoryRepository.save(category);
        return mapToResponseDto(updated);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        List<CategoryResponseDto> subCategories = category.getSubCategories().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new CategoryResponseDto(
                category.getCategoryId(),
                category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null,
                subCategories
        );
    }
}
