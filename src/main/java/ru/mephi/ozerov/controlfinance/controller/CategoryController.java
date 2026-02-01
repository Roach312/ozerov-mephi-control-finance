package ru.mephi.ozerov.controlfinance.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryResponse;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.service.CategoryService;

/** Контроллер для операций с категориями. */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Создать новую категорию.
     *
     * @param request данные для создания категории
     * @return ответ созданной категории
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить все категории текущего пользователя.
     *
     * @param type опциональный фильтр по типу категории
     * @return список ответов категорий
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @RequestParam(required = false) CategoryType type) {
        List<CategoryResponse> responses;
        if (type != null) {
            responses = categoryService.getCategoriesByType(type);
        } else {
            responses = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(responses);
    }
}
