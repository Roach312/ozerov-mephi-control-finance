package ru.mephi.ozerov.controlfinance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.entity.Wallet;

/** Репозиторий для операций с сущностью Wallet. */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Найти кошелёк по пользователю.
     *
     * @param user владелец кошелька
     * @return Optional, содержащий кошелёк, если найден
     */
    Optional<Wallet> findByUser(User user);

    /**
     * Найти кошелёк по id пользователя.
     *
     * @param userId id пользователя
     * @return Optional, содержащий кошелёк, если найден
     */
    Optional<Wallet> findByUserId(Long userId);
}
