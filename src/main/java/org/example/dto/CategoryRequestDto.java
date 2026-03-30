package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a category")
public class CategoryRequestDto {

    @NotBlank(message = "Category name is required")
    @Schema(description = "Name of the category", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "ID of the parent category. Omit or set to null for a top-level category.", example = "1")
    private Long parentCategoryId;
}
