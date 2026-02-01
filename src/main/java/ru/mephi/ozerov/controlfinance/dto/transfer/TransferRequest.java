package ru.mephi.ozerov.controlfinance.dto.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for transfer creation request. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotBlank(message = "Recipient login is required")
    private String toUserLogin;

    @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") private BigDecimal amount;
}
