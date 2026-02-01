package ru.mephi.ozerov.controlfinance.service;

import ru.mephi.ozerov.controlfinance.dto.auth.AuthResponse;
import ru.mephi.ozerov.controlfinance.dto.auth.LoginRequest;
import ru.mephi.ozerov.controlfinance.dto.auth.RegisterRequest;
import ru.mephi.ozerov.controlfinance.entity.User;

/** Интерфейс сервиса для операций с пользователями. */
public interface UserService {

    /**
     * Регистрация нового пользователя.
     *
     * @param request данные регистрации
     * @return ответ аутентификации
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Аутентификация пользователя.
     *
     * @param request данные для входа
     * @return ответ аутентификации
     */
    AuthResponse login(LoginRequest request);

    /**
     * Получить пользователя по логину.
     *
     * @param login логин пользователя
     * @return сущность пользователя
     */
    User getUserByLogin(String login);

    /**
     * Получить текущего аутентифицированного пользователя.
     *
     * @return сущность текущего пользователя
     */
    User getCurrentUser();
}
