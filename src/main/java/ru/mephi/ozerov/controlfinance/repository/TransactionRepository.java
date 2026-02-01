package ru.mephi.ozerov.controlfinance.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.Transaction;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;
import ru.mephi.ozerov.controlfinance.entity.Wallet;

/** Репозиторий для операций с сущностью Transaction. */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Найти все транзакции кошелька.
     *
     * @param wallet кошелёк
     * @return список транзакций
     */
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    /**
     * Найти все транзакции кошелька по id кошелька.
     *
     * @param walletId id кошелька
     * @return список транзакций
     */
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    /**
     * Найти транзакции по кошельку и типу.
     *
     * @param wallet кошелёк
     * @param type тип транзакции
     * @return список транзакций
     */
    List<Transaction> findByWalletAndType(Wallet wallet, TransactionType type);

    /**
     * Найти транзакции по кошельку и категории.
     *
     * @param wallet кошелёк
     * @param category категория
     * @return список транзакций
     */
    List<Transaction> findByWalletAndCategory(Wallet wallet, Category category);

    /**
     * Найти транзакции кошелька в диапазоне дат.
     *
     * @param wallet кошелёк
     * @param start начальная дата
     * @param end конечная дата
     * @return список транзакций
     */
    List<Transaction> findByWalletAndCreatedAtBetween(
            Wallet wallet, LocalDateTime start, LocalDateTime end);

    /**
     * Вычислить сумму транзакций по кошельку и типу.
     *
     * @param walletId id кошелька
     * @param type тип транзакции
     * @return сумма
     */
    @Query(
            "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.type = :type")
    BigDecimal sumByWalletIdAndType(
            @Param("walletId") Long walletId, @Param("type") TransactionType type);

    /**
     * Вычислить сумму транзакций по кошельку, типу и диапазону дат.
     *
     * @param walletId id кошелька
     * @param type тип транзакции
     * @param start начальная дата
     * @param end конечная дата
     * @return сумма
     */
    @Query(
            "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.type = :type AND t.createdAt BETWEEN :start AND :end")
    BigDecimal sumByWalletIdAndTypeAndDateRange(
            @Param("walletId") Long walletId,
            @Param("type") TransactionType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Вычислить сумму транзакций по кошельку и категории.
     *
     * @param walletId id кошелька
     * @param categoryId id категории
     * @return сумма
     */
    @Query(
            "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.category.id = :categoryId")
    BigDecimal sumByWalletIdAndCategoryId(
            @Param("walletId") Long walletId, @Param("categoryId") Long categoryId);

    /**
     * Найти транзакции по кошельку и категориям.
     *
     * @param walletId id кошелька
     * @param categoryIds список id категорий
     * @return список транзакций
     */
    @Query(
            "SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.category.id IN :categoryIds ORDER BY t.createdAt DESC")
    List<Transaction> findByWalletIdAndCategoryIds(
            @Param("walletId") Long walletId, @Param("categoryIds") List<Long> categoryIds);
}
