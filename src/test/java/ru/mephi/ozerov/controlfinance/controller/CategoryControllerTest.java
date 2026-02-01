package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryResponse;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.service.CategoryService;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CategoryService categoryService;

    @Test
    void createCategory_ShouldReturnCreated() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);

        CategoryResponse response =
                CategoryResponse.builder().id(1L).name("Food").type(CategoryType.EXPENSE).build();

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    void getCategories_ShouldReturnList() throws Exception {
        List<CategoryResponse> responses =
                Arrays.asList(
                        CategoryResponse.builder()
                                .id(1L)
                                .name("Food")
                                .type(CategoryType.EXPENSE)
                                .build(),
                        CategoryResponse.builder()
                                .id(2L)
                                .name("Salary")
                                .type(CategoryType.INCOME)
                                .build());

        when(categoryService.getAllCategories()).thenReturn(responses);

        mockMvc.perform(get("/api/categories").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCategories_ByType_ShouldReturnFilteredList() throws Exception {
        List<CategoryResponse> responses =
                Arrays.asList(
                        CategoryResponse.builder()
                                .id(1L)
                                .name("Food")
                                .type(CategoryType.EXPENSE)
                                .build());

        when(categoryService.getCategoriesByType(CategoryType.EXPENSE)).thenReturn(responses);

        mockMvc.perform(get("/api/categories").with(user("testuser")).param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"));
    }
}
