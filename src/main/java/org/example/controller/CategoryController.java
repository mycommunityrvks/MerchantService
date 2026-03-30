package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CategoryRequestDto;
import org.example.dto.CategoryResponseDto;
import org.example.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "CRUD operations for managing product categories and sub-categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            description = "Creates a top-level category when `parentCategoryId` is omitted or null. " +
                          "Provide a valid `parentCategoryId` to create a sub-category under an existing category."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Top-level category",
                                    value = """
                                            {
                                              "categoryId": 1,
                                              "name": "Electronics",
                                              "parentCategoryId": null,
                                              "subCategories": []
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error - required field missing or blank",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "Category name is required"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A category with the same name already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-06-01T10:00:00",
                                              "message": "Category with name 'Electronics' already exists",
                                              "details": "uri=/api/categories"
                                            }"""
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category data to create",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Top-level category",
                                            summary = "Create a root category with no parent",
                                            value = """
                                                    {
                                                      "name": "Electronics"
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            name = "Sub-category",
                                            summary = "Create a sub-category under category ID 1",
                                            value = """
                                                    {
                                                      "name": "Mobile Phones",
                                                      "parentCategoryId": 1
                                                    }"""
                                    )
                            }
                    )
            )
            @Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto response = categoryService.createCategory(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all top-level categories",
            description = "Returns all top-level (root) categories with their full sub-category hierarchy nested recursively."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of top-level categories retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDto.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "categoryId": 1,
                                                "name": "Electronics",
                                                "parentCategoryId": null,
                                                "subCategories": [
                                                  {
                                                    "categoryId": 2,
                                                    "name": "Mobile Phones",
                                                    "parentCategoryId": 1,
                                                    "subCategories": []
                                                  },
                                                  {
                                                    "categoryId": 3,
                                                    "name": "Laptops",
                                                    "parentCategoryId": 1,
                                                    "subCategories": []
                                                  }
                                                ]
                                              },
                                              {
                                                "categoryId": 4,
                                                "name": "Clothing",
                                                "parentCategoryId": null,
                                                "subCategories": []
                                              }
                                            ]"""
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Get a category by ID",
            description = "Retrieves a single category by its unique identifier, including its sub-category hierarchy."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "categoryId": 1,
                                              "name": "Electronics",
                                              "parentCategoryId": null,
                                              "subCategories": [
                                                {
                                                  "categoryId": 2,
                                                  "name": "Mobile Phones",
                                                  "parentCategoryId": 1,
                                                  "subCategories": []
                                                }
                                              ]
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-06-01T10:00:00",
                                              "message": "Category not found with id: 99",
                                              "details": "uri=/api/categories/99"
                                            }"""
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @Parameter(description = "Unique identifier of the category to retrieve", example = "1", required = true)
            @PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Update an existing category",
            description = "Updates the name and/or parent of an existing category identified by its ID. " +
                          "Set `parentCategoryId` to null to promote the category to top-level."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "categoryId": 2,
                                              "name": "Smartphones",
                                              "parentCategoryId": 1,
                                              "subCategories": []
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error - required field missing or blank",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "Category name is required"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-06-01T10:00:00",
                                              "message": "Category not found with id: 99",
                                              "details": "uri=/api/categories/99"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A category with the same name already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-06-01T10:00:00",
                                              "message": "Category with name 'Smartphones' already exists",
                                              "details": "uri=/api/categories/2"
                                            }"""
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Parameter(description = "Unique identifier of the category to update", example = "2", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated category data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Rename category",
                                            summary = "Rename a sub-category without changing its parent",
                                            value = """
                                                    {
                                                      "name": "Smartphones",
                                                      "parentCategoryId": 1
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            name = "Promote to top-level",
                                            summary = "Remove the parent to make the category top-level",
                                            value = """
                                                    {
                                                      "name": "Smartphones"
                                                    }"""
                                    )
                            }
                    )
            )
            @Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto updated = categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a category",
            description = "Permanently deletes a category and all of its sub-categories by the specified ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-06-01T10:00:00",
                                              "message": "Category not found with id: 99",
                                              "details": "uri=/api/categories/99"
                                            }"""
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Unique identifier of the category to delete", example = "1", required = true)
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
