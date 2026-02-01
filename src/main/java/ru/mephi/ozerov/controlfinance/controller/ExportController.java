package ru.mephi.ozerov.controlfinance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.ozerov.controlfinance.service.ExportService;

/**
 * Контроллер для операций экспорта.
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    /**
     * Экспортировать финансовую сводку.
     * @param format формат вывода (text или json, по умолчанию json)
     * @return экспортированные данные
     */
    @GetMapping("/summary")
    public ResponseEntity<String> exportSummary(@RequestParam(defaultValue = "json") String format) {
        String content;
        MediaType mediaType;
        String filename;

        if ("text".equalsIgnoreCase(format)) {
            content = exportService.exportSummaryAsText();
            mediaType = MediaType.TEXT_PLAIN;
            filename = "financial_summary.txt";
        } else {
            content = exportService.exportSummaryAsJson();
            mediaType = MediaType.APPLICATION_JSON;
            filename = "financial_summary.json";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(content);
    }
}
