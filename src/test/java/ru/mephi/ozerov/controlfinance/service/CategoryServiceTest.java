package ru.mephi.ozerov.controlfinance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryResponse;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.exception.EntityAlreadyExistsException;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.CategoryRepository;
import ru.mephi.ozerov.controlfinance.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;

    @Mock private UserService userService;

    @InjectMocks private CategoryServiceImpl categoryService;

    private User user;
    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).login("testuser").build();

        category =
                Category.builder()
                        .id(1L)
                        .user(user)
                        .name("Food")
                        .type(CategoryType.EXPENSE)
                        .build();

        categoryRequest = CategoryRequest.builder().name("Food").type(CategoryType.EXPENSE).build();
    }

    @Test
    void createCategory_ShouldCreateAndReturnCategory() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.existsByUserAndNameAndType(user, "Food", CategoryType.EXPENSE))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        assertNotNull(response);
        assertEquals("Food", response.getName());
        assertEquals(CategoryType.EXPENSE, response.getType());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowExceptionWhenCategoryExists() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.existsByUserAndNameAndType(user, "Food", CategoryType.EXPENSE))
                .thenReturn(true);

        assertThrows(
                EntityAlreadyExistsException.class,
                () -> categoryService.createCategory(categoryRequest));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByUser(user)).thenReturn(List.of(category));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Food", responses.get(0).getName());
    }

    @Test
    void getCategoriesByType_ShouldReturnFilteredCategories() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByUserAndType(user, CategoryType.EXPENSE))
                .thenReturn(List.of(category));

        List<CategoryResponse> responses =
                categoryService.getCategoriesByType(CategoryType.EXPENSE);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(CategoryType.EXPENSE, responses.get(0).getType());
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCategoryById_ShouldThrowExceptionWhenNotFound() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }
}
