package ru.mephi.ozerov.controlfinance.service;

import ru.mephi.ozerov.controlfinance.dto.wallet.WalletResponse;
import ru.mephi.ozerov.controlfinance.entity.Wallet;

/**
 * Интерфейс сервиса для операций с кошельками.
 */
public interface WalletService {

    /**
     * Получить кошелёк текущего пользователя.
     * @return DTO ответа кошелька
     */
    WalletResponse getCurrentUserWallet();

    /**
     * Получить сущность кошелька текущего пользователя.
     * @return сущность кошелька
     */
    Wallet getCurrentUserWalletEntity();

    /**
     * Получить сущность кошелька конкретного пользователя.
     * @param login логин пользователя
     * @return сущность кошелька
     */
    Wallet getWalletByUserLogin(String login);
}
