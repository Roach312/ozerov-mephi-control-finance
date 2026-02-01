package ru.mephi.ozerov.controlfinance.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;

import java.math.BigDecimal;

/**
 * DTO for category summary response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryResponse {

    private Long categoryId;
    private String categoryName;
    private CategoryType categoryType;
    private BigDecimal totalAmount;
}
