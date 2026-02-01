package ru.mephi.ozerov.controlfinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для операций с сущностью Category.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Найти все категории пользователя.
     * @param user владелец категорий
     * @return список категорий
     */
    List<Category> findByUser(User user);

    /**
     * Найти все категории пользователя по типу.
     * @param user владелец категорий
     * @param type тип категории (INCOME/EXPENSE)
     * @return список категорий
     */
    List<Category> findByUserAndType(User user, CategoryType type);

    /**
     * Найти все категории пользователя по id пользователя.
     * @param userId id пользователя
     * @return список категорий
     */
    List<Category> findByUserId(Long userId);

    /**
     * Найти категорию по пользователю, имени и типу.
     * @param user владелец категории
     * @param name имя категории
     * @param type тип категории
     * @return Optional, содержащий категорию, если найдена
     */
    Optional<Category> findByUserAndNameAndType(User user, String name, CategoryType type);

    /**
     * Проверить, существует ли категория у пользователя с данным именем и типом.
     * @param user пользователь
     * @param name имя категории
     * @param type тип категории
     * @return true, если существует
     */
    boolean existsByUserAndNameAndType(User user, String name, CategoryType type);

    /**
     * Найти категорию по id и пользователю.
     * @param id id категории
     * @param user владелец категории
     * @return Optional, содержащий категорию, если найдена
     */
    Optional<Category> findByIdAndUser(Long id, User user);
}
