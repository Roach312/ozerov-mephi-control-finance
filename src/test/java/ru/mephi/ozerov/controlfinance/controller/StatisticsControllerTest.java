package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.service.StatisticsService;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private StatisticsService statisticsService;

    @Test
    void getSummary_ShouldReturnSummary() throws Exception {
        SummaryResponse response =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("5000.00"))
                        .totalExpense(new BigDecimal("3000.00"))
                        .balance(new BigDecimal("2000.00"))
                        .build();

        when(statisticsService.getSummary()).thenReturn(response);

        mockMvc.perform(get("/api/statistics/summary").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(5000.00))
                .andExpect(jsonPath("$.totalExpense").value(3000.00))
                .andExpect(jsonPath("$.balance").value(2000.00));
    }

    @Test
    void getSummaryByCategories_ShouldReturnList() throws Exception {
        List<CategorySummaryResponse> responses =
                Arrays.asList(
                        CategorySummaryResponse.builder()
                                .categoryId(1L)
                                .categoryName("Food")
                                .categoryType(CategoryType.EXPENSE)
                                .totalAmount(new BigDecimal("500.00"))
                                .build());

        when(statisticsService.getSummaryByCategories(null)).thenReturn(responses);

        mockMvc.perform(get("/api/statistics/by-categories").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoryName").value("Food"));
    }

    @Test
    void getSummaryByCategories_WithIds_ShouldReturnFilteredList() throws Exception {
        List<Long> categoryIds = Arrays.asList(1L, 2L);
        List<CategorySummaryResponse> responses =
                Arrays.asList(
                        CategorySummaryResponse.builder()
                                .categoryId(1L)
                                .categoryName("Food")
                                .categoryType(CategoryType.EXPENSE)
                                .totalAmount(new BigDecimal("500.00"))
                                .build(),
                        CategorySummaryResponse.builder()
                                .categoryId(2L)
                                .categoryName("Transport")
                                .categoryType(CategoryType.EXPENSE)
                                .totalAmount(new BigDecimal("200.00"))
                                .build());

        when(statisticsService.getSummaryByCategories(categoryIds)).thenReturn(responses);

        mockMvc.perform(
                        get("/api/statistics/by-categories")
                                .with(user("testuser"))
                                .param("categoryIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getBudgetStatus_ShouldReturnList() throws Exception {
        List<BudgetStatusResponse> responses =
                Arrays.asList(
                        BudgetStatusResponse.builder()
                                .categoryId(1L)
                                .categoryName("Food")
                                .limitAmount(new BigDecimal("500.00"))
                                .spentAmount(new BigDecimal("300.00"))
                                .remainingAmount(new BigDecimal("200.00"))
                                .limitExceeded(false)
                                .build());

        when(statisticsService.getBudgetStatus()).thenReturn(responses);

        mockMvc.perform(get("/api/statistics/budget-status").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].limitExceeded").value(false));
    }
}
