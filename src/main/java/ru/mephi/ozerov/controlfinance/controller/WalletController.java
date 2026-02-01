package ru.mephi.ozerov.controlfinance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.ozerov.controlfinance.dto.wallet.WalletResponse;
import ru.mephi.ozerov.controlfinance.service.WalletService;

/** Контроллер для операций с кошельком. */
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Получить кошелёк текущего пользователя.
     *
     * @return ответ кошелька
     */
    @GetMapping
    public ResponseEntity<WalletResponse> getWallet() {
        WalletResponse response = walletService.getCurrentUserWallet();
        return ResponseEntity.ok(response);
    }
}
