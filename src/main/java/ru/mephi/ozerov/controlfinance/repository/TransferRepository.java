package ru.mephi.ozerov.controlfinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.Transfer;
import ru.mephi.ozerov.controlfinance.entity.Wallet;

import java.util.List;

/**
 * Репозиторий для операций с сущностью Transfer.
 */
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * Найти все переводы, отправленные с кошелька.
     * @param fromWallet кошелёк отправителя
     * @return список переводов
     */
    List<Transfer> findByFromWalletOrderByCreatedAtDesc(Wallet fromWallet);

    /**
     * Найти все переводы, полученные на кошелёк.
     * @param toWallet кошелёк получателя
     * @return список переводов
     */
    List<Transfer> findByToWalletOrderByCreatedAtDesc(Wallet toWallet);

    /**
     * Найти все переводы с участием кошелька (отправленные или полученные).
     * @param fromWallet кошелёк
     * @param toWallet кошелёк
     * @return список переводов
     */
    List<Transfer> findByFromWalletOrToWalletOrderByCreatedAtDesc(Wallet fromWallet, Wallet toWallet);

    /**
     * Найти все переводы по id кошелька отправителя.
     * @param fromWalletId id кошелька отправителя
     * @return список переводов
     */
    List<Transfer> findByFromWalletIdOrderByCreatedAtDesc(Long fromWalletId);

    /**
     * Найти все переводы по id кошелька получателя.
     * @param toWalletId id кошелька получателя
     * @return список переводов
     */
    List<Transfer> findByToWalletIdOrderByCreatedAtDesc(Long toWalletId);
}
