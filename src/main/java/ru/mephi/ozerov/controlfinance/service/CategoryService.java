package ru.mephi.ozerov.controlfinance.service;

import java.util.List;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryResponse;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;

/** Интерфейс сервиса для операций с категориями. */
public interface CategoryService {

    /**
     * Создать новую категорию для текущего пользователя.
     *
     * @param request данные для создания категории
     * @return ответ созданной категории
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Получить все категории текущего пользователя.
     *
     * @return список ответов категорий
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Получить категории по типу для текущего пользователя.
     *
     * @param type фильтр по типу категории
     * @return список ответов категорий
     */
    List<CategoryResponse> getCategoriesByType(CategoryType type);

    /**
     * Получить сущность категории по id для текущего пользователя.
     *
     * @param categoryId id категории
     * @return сущность категории
     */
    Category getCategoryById(Long categoryId);
}
