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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionRequest;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;
import ru.mephi.ozerov.controlfinance.service.TransactionService;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TransactionService transactionService;

    @Test
    void createTransaction_ShouldReturnCreated() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setType(TransactionType.EXPENSE);
        request.setCategoryId(1L);
        request.setDescription("Lunch");

        TransactionResponse response =
                TransactionResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("100.00"))
                        .type(TransactionType.EXPENSE)
                        .categoryId(1L)
                        .categoryName("Food")
                        .description("Lunch")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transactions")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getTransactions_ShouldReturnList() throws Exception {
        List<TransactionResponse> responses =
                Arrays.asList(
                        TransactionResponse.builder()
                                .id(1L)
                                .amount(new BigDecimal("100.00"))
                                .type(TransactionType.EXPENSE)
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transactionService.getAllTransactions()).thenReturn(responses);

        mockMvc.perform(get("/api/transactions").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTransactions_ByType_ShouldReturnFilteredList() throws Exception {
        List<TransactionResponse> responses =
                Arrays.asList(
                        TransactionResponse.builder()
                                .id(1L)
                                .amount(new BigDecimal("100.00"))
                                .type(TransactionType.INCOME)
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transactionService.getTransactionsByType(TransactionType.INCOME))
                .thenReturn(responses);

        mockMvc.perform(get("/api/transactions").with(user("testuser")).param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }

    @Test
    void getTransactions_ByCategory_ShouldReturnFilteredList() throws Exception {
        List<TransactionResponse> responses =
                Arrays.asList(
                        TransactionResponse.builder()
                                .id(1L)
                                .amount(new BigDecimal("100.00"))
                                .type(TransactionType.EXPENSE)
                                .categoryId(1L)
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transactionService.getTransactionsByCategory(1L)).thenReturn(responses);

        mockMvc.perform(get("/api/transactions").with(user("testuser")).param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1));
    }
}
