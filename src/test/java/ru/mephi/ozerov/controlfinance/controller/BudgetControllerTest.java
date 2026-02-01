package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetRequest;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetResponse;
import ru.mephi.ozerov.controlfinance.service.BudgetService;

@SpringBootTest
@AutoConfigureMockMvc
class BudgetControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private BudgetService budgetService;

    @Test
    void createBudget_ShouldReturnCreated() throws Exception {
        BudgetRequest request = new BudgetRequest();
        request.setCategoryId(1L);
        request.setLimitAmount(new BigDecimal("500.00"));

        BudgetResponse response =
                BudgetResponse.builder()
                        .id(1L)
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("500.00"))
                        .build();

        when(budgetService.createOrUpdateBudget(any(BudgetRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post("/api/budgets")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.limitAmount").value(500.00));
    }

    @Test
    void getBudgets_ShouldReturnList() throws Exception {
        List<BudgetResponse> responses =
                Arrays.asList(
                        BudgetResponse.builder()
                                .id(1L)
                                .categoryId(1L)
                                .categoryName("Food")
                                .limitAmount(new BigDecimal("500.00"))
                                .build());

        when(budgetService.getAllBudgets()).thenReturn(responses);

        mockMvc.perform(get("/api/budgets").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBudgetByCategory_ShouldReturnBudget() throws Exception {
        BudgetResponse response =
                BudgetResponse.builder()
                        .id(1L)
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("500.00"))
                        .build();

        when(budgetService.getBudgetByCategory(1L)).thenReturn(response);

        mockMvc.perform(get("/api/budgets/category/1").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    void getBudgetByCategory_NotFound_ShouldReturn404() throws Exception {
        when(budgetService.getBudgetByCategory(999L)).thenReturn(null);

        mockMvc.perform(get("/api/budgets/category/999").with(user("testuser")))
                .andExpect(status().isNotFound());
    }
}
