package ru.mephi.ozerov.controlfinance.service;

/**
 * Интерфейс сервиса для операций экспорта.
 */
public interface ExportService {

    /**
     * Экспортировать финансовую сводку в текстовом формате.
     * @return форматированная текстовая сводка
     */
    String exportSummaryAsText();

    /**
     * Экспортировать финансовую сводку в формате JSON.
     * @return JSON сводка
     */
    String exportSummaryAsJson();
}
