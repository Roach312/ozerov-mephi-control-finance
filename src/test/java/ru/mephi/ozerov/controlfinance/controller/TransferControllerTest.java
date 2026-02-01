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
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferRequest;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferResponse;
import ru.mephi.ozerov.controlfinance.service.TransferService;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TransferService transferService;

    @Test
    void createTransfer_ShouldReturnCreated() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setToUserLogin("recipient");
        request.setAmount(new BigDecimal("100.00"));

        TransferResponse response =
                TransferResponse.builder()
                        .id(1L)
                        .fromUserLogin("testuser")
                        .toUserLogin("recipient")
                        .amount(new BigDecimal("100.00"))
                        .createdAt(LocalDateTime.now())
                        .build();

        when(transferService.createTransfer(any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post("/api/transfers")
                                .with(user("testuser"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.toUserLogin").value("recipient"));
    }

    @Test
    void getTransfers_ShouldReturnList() throws Exception {
        List<TransferResponse> responses =
                Arrays.asList(
                        TransferResponse.builder()
                                .id(1L)
                                .fromUserLogin("testuser")
                                .toUserLogin("recipient")
                                .amount(new BigDecimal("100.00"))
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transferService.getAllTransfers()).thenReturn(responses);

        mockMvc.perform(get("/api/transfers").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getSentTransfers_ShouldReturnList() throws Exception {
        List<TransferResponse> responses =
                Arrays.asList(
                        TransferResponse.builder()
                                .id(1L)
                                .fromUserLogin("testuser")
                                .toUserLogin("recipient")
                                .amount(new BigDecimal("100.00"))
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transferService.getSentTransfers()).thenReturn(responses);

        mockMvc.perform(get("/api/transfers/sent").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fromUserLogin").value("testuser"));
    }

    @Test
    void getReceivedTransfers_ShouldReturnList() throws Exception {
        List<TransferResponse> responses =
                Arrays.asList(
                        TransferResponse.builder()
                                .id(1L)
                                .fromUserLogin("sender")
                                .toUserLogin("testuser")
                                .amount(new BigDecimal("100.00"))
                                .createdAt(LocalDateTime.now())
                                .build());

        when(transferService.getReceivedTransfers()).thenReturn(responses);

        mockMvc.perform(get("/api/transfers/received").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toUserLogin").value("testuser"));
    }
}
