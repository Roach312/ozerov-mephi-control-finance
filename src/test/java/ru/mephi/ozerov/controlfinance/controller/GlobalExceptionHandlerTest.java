package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.category.CategoryRequest;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.exception.EntityAlreadyExistsException;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.service.CategoryService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CategoryService categoryService;

    @MockitoBean private WalletService walletService;

    @Test
    void handleEntityNotFoundException_ShouldReturn404() throws Exception {
        when(walletService.getCurrentUserWallet())
                .thenThrow(new EntityNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/wallet").with(user("testuser")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }

    @Test
    void handleEntityAlreadyExistsException_ShouldReturn409() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);

        when(categoryService.createCategory(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new EntityAlreadyExistsException("Category already exists"));

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Category already exists"));
    }

    @Test
    void handleValidationException_ShouldReturn400() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType(CategoryType.EXPENSE);

        when(categoryService.createCategory(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new ValidationException("Invalid category data"));

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid category data"));
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturn400WithFieldErrors() throws Exception {
        CategoryRequest request = new CategoryRequest();
        // name is null, should fail validation

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void handleGenericException_ShouldReturn500() throws Exception {
        when(walletService.getCurrentUserWallet())
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/wallet").with(user("testuser")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
