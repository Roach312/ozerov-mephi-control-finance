package ru.mephi.ozerov.controlfinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.Budget;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.Wallet;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для операций с сущностью Budget.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Найти все бюджеты кошелька.
     * @param wallet кошелёк
     * @return список бюджетов
     */
    List<Budget> findByWallet(Wallet wallet);

    /**
     * Найти все бюджеты кошелька по id кошелька.
     * @param walletId id кошелька
     * @return список бюджетов
     */
    List<Budget> findByWalletId(Long walletId);

    /**
     * Найти бюджет по кошельку и категории.
     * @param wallet кошелёк
     * @param category категория
     * @return Optional, содержащий бюджет, если найден
     */
    Optional<Budget> findByWalletAndCategory(Wallet wallet, Category category);

    /**
     * Найти бюджет по id кошелька и id категории.
     * @param walletId id кошелька
     * @param categoryId id категории
     * @return Optional, содержащий бюджет, если найден
     */
    Optional<Budget> findByWalletIdAndCategoryId(Long walletId, Long categoryId);

    /**
     * Проверить, существует ли бюджет для кошелька и категории.
     * @param wallet кошелёк
     * @param category категория
     * @return true, если существует
     */
    boolean existsByWalletAndCategory(Wallet wallet, Category category);
}
