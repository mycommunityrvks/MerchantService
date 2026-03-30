package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category details returned in API responses")
public class CategoryResponseDto {

    @Schema(description = "Unique identifier of the category", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "ID of the parent category. Null for top-level categories.", example = "null")
    private Long parentCategoryId;

    @Schema(description = "List of sub-categories nested under this category")
    private List<CategoryResponseDto> subCategories;
}
