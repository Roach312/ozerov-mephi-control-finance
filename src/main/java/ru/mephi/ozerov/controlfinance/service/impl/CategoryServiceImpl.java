package ru.mephi.ozerov.controlfinance.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryResponse;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.exception.EntityAlreadyExistsException;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.CategoryRepository;
import ru.mephi.ozerov.controlfinance.service.CategoryService;
import ru.mephi.ozerov.controlfinance.service.UserService;

/** Реализация сервиса категорий. */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        User user = userService.getCurrentUser();

        if (categoryRepository.existsByUserAndNameAndType(
                user, request.getName(), request.getType())) {
            throw new EntityAlreadyExistsException(
                    "Category '"
                            + request.getName()
                            + "' with type "
                            + request.getType()
                            + " already exists");
        }

        Category category =
                Category.builder()
                        .user(user)
                        .name(request.getName())
                        .type(request.getType())
                        .build();

        category = categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        User user = userService.getCurrentUser();
        return categoryRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(CategoryType type) {
        User user = userService.getCurrentUser();
        return categoryRepository.findByUserAndType(user, type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        User user = userService.getCurrentUser();
        return categoryRepository
                .findByIdAndUser(categoryId, user)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Category not found with id: " + categoryId));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}
