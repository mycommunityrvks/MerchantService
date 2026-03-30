package org.example.service;

import lombok.RequiredArgsConstructor;
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

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
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
