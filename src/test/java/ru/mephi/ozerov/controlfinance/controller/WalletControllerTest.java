package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.wallet.WalletResponse;
import ru.mephi.ozerov.controlfinance.service.WalletService;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private WalletService walletService;

    @Test
    void getWallet_ShouldReturnWallet() throws Exception {
        WalletResponse response =
                WalletResponse.builder().id(1L).balance(new BigDecimal("1000.00")).build();

        when(walletService.getCurrentUserWallet()).thenReturn(response);

        mockMvc.perform(get("/api/wallet").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void getWallet_Unauthorized_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/wallet")).andExpect(status().isUnauthorized());
    }
}
