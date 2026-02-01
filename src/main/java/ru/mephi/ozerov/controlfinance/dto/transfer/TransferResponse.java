package ru.mephi.ozerov.controlfinance.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transfer response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private Long id;
    private String fromUserLogin;
    private String toUserLogin;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
