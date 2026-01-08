package com.pranshudev.spendlytics.controller;

import com.pranshudev.spendlytics.dto.CategoryDTO;
import com.pranshudev.spendlytics.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping("/save")
    public ResponseEntity<CategoryDTO> saveCategory(
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);


    }

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getCategoryForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(categories);

    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesByType(@PathVariable String type) {
        List<CategoryDTO> categories = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.status(HttpStatus.OK).body(categories);

    }

    // ---------------- PATCH ----------------
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO categoryDTO
    ) {
        return ResponseEntity.ok(
                categoryService.updateByCategory(categoryId, categoryDTO)
        );
    }

    // ---------------- PUT ----------------
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategoryPut(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO categoryDTO
    ) {
        return ResponseEntity.ok(
                categoryService.updateCategoryPut(categoryId, categoryDTO)
        );
    }
}
