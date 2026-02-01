package ru.mephi.ozerov.controlfinance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.wallet.WalletResponse;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.UserService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

/**
 * Реализация сервиса кошельков.
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getCurrentUserWallet() {
        Wallet wallet = getCurrentUserWalletEntity();
        return WalletResponse.builder()
                .id(wallet.getId())
                .userLogin(wallet.getUser().getLogin())
                .balance(wallet.getBalance())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getCurrentUserWalletEntity() {
        User user = userService.getCurrentUser();
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + user.getLogin()));
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByUserLogin(String login) {
        User user = userService.getUserByLogin(login);
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + login));
    }
}
