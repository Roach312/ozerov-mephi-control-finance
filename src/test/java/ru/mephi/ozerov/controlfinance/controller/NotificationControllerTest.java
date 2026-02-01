package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.notification.NotificationResponse;
import ru.mephi.ozerov.controlfinance.service.NotificationService;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private NotificationService notificationService;

    @Test
    void getNotifications_ShouldReturnList() throws Exception {
        List<NotificationResponse> responses =
                Arrays.asList(
                        NotificationResponse.builder()
                                .type("BUDGET_EXCEEDED")
                                .message("Budget limit exceeded for category 'Food'")
                                .severity("WARNING")
                                .build(),
                        NotificationResponse.builder()
                                .type("EXPENSES_EXCEED_INCOME")
                                .message("Your expenses exceed your income")
                                .severity("WARNING")
                                .build());

        when(notificationService.getNotifications()).thenReturn(responses);

        mockMvc.perform(get("/api/notifications").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("BUDGET_EXCEEDED"))
                .andExpect(jsonPath("$[1].type").value("EXPENSES_EXCEED_INCOME"));
    }

    @Test
    void getNotifications_Empty_ShouldReturnEmptyList() throws Exception {
        when(notificationService.getNotifications()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
