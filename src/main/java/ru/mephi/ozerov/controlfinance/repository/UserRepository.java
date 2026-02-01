package ru.mephi.ozerov.controlfinance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.User;

/** Репозиторий для операций с сущностью User. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Найти пользователя по логину.
     *
     * @param login логин пользователя
     * @return Optional, содержащий пользователя, если найден
     */
    Optional<User> findByLogin(String login);

    /**
     * Проверить, существует ли пользователь с данным логином.
     *
     * @param login логин для проверки
     * @return true, если пользователь существует
     */
    boolean existsByLogin(String login);
}
