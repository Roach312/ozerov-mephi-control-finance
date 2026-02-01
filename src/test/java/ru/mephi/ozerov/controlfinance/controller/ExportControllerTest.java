package ru.mephi.ozerov.controlfinance.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.service.ExportService;

@SpringBootTest
@AutoConfigureMockMvc
class ExportControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ExportService exportService;

    @Test
    void exportSummary_Json_ShouldReturnJson() throws Exception {
        String jsonContent = "{\"totalIncome\":5000,\"totalExpense\":3000}";
        when(exportService.exportSummaryAsJson()).thenReturn(jsonContent);

        mockMvc.perform(get("/api/export/summary").with(user("testuser")).param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        header().string(
                                        "Content-Disposition",
                                        "attachment; filename=\"financial_summary.json\""))
                .andExpect(content().string(jsonContent));
    }

    @Test
    void exportSummary_Text_ShouldReturnText() throws Exception {
        String textContent = "Total Income: 5000\nTotal Expense: 3000";
        when(exportService.exportSummaryAsText()).thenReturn(textContent);

        mockMvc.perform(get("/api/export/summary").with(user("testuser")).param("format", "text"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(
                        header().string(
                                        "Content-Disposition",
                                        "attachment; filename=\"financial_summary.txt\""))
                .andExpect(content().string(textContent));
    }

    @Test
    void exportSummary_DefaultFormat_ShouldReturnJson() throws Exception {
        String jsonContent = "{\"totalIncome\":5000}";
        when(exportService.exportSummaryAsJson()).thenReturn(jsonContent);

        mockMvc.perform(get("/api/export/summary").with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
