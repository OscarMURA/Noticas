package com.trade.icesi_trade.controller.api;

import com.trade.icesi_trade.Service.Interface.CategoryService;
import com.trade.icesi_trade.dtos.CategoryDto;
import com.trade.icesi_trade.mappers.CategoryMapper;
import com.trade.icesi_trade.model.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "CRUD operations for product categories")
@CrossOrigin
public class CategoryApiController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryDto>> getAll() {
        List<CategoryDto> categories = categoryService.getAllCategories().stream()
                .map(categoryMapper::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.entityToDto(category));
    }

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        Category created = categoryService.createCategory(categoryMapper.dtoToEntity(dto));
        return new ResponseEntity<>(categoryMapper.entityToDto(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing category")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        Category updated = categoryService.updateCategory(id, categoryMapper.dtoToEntity(dto));
        return ResponseEntity.ok(categoryMapper.entityToDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully.");
    }
}
