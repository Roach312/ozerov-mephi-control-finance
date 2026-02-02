# Control Finance

Backend-приложение для управления личными финансами.

## Технологии

- Java 17
- Spring Boot 4.0.2
- Spring Security (сессионная аутентификация, BCrypt)
- Spring Data JPA + Hibernate
- H2 Database (in-memory)
- Maven
- JaCoCo (покрытие тестов >80%)

## Функциональность

- **Авторизация** — регистрация и вход по логину/паролю
- **Кошелёк** — автоматическое создание при регистрации, отслеживание баланса
- **Категории** — создание категорий доходов и расходов
- **Транзакции** — добавление доходов и расходов с привязкой к категориям
- **Бюджеты** — установка лимитов на категории расходов
- **Статистика** — общие суммы, суммы по категориям, статус бюджетов
- **Оповещения** — уведомления о превышении бюджета и когда расходы превышают доходы
- **Переводы** — переводы между пользователями по логину
- **Экспорт** — выгрузка финансовой сводки в JSON или текстовый формат

## Запуск

```bash
# Сборка проекта
./mvnw clean package -DskipTests

# Запуск приложения
./mvnw spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:finance`
- User: `sa`
- Password: (пустой)

## Тестирование

```bash
# Запуск тестов
./mvnw test

# Отчёт о покрытии
./mvnw jacoco:report
# Отчёт доступен в target/site/jacoco/index.html
```

## API Endpoints

### Аутентификация

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/auth/register` | Регистрация нового пользователя |
| POST | `/api/auth/login` | Вход в систему |

**Пример регистрации:**
```json
POST /api/auth/register
{
  "login": "user1",
  "password": "password123"
}
```

### Кошелёк

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/wallet` | Получить кошелёк текущего пользователя |

### Категории

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/categories` | Получить все категории |
| GET | `/api/categories?type=EXPENSE` | Получить категории по типу |
| POST | `/api/categories` | Создать категорию |

**Пример создания категории:**
```json
POST /api/categories
{
  "name": "Продукты",
  "type": "EXPENSE"
}
```

Типы категорий: `INCOME`, `EXPENSE`

### Транзакции

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/transactions` | Получить все транзакции |
| GET | `/api/transactions?type=INCOME` | Фильтр по типу |
| GET | `/api/transactions?categoryId=1` | Фильтр по категории |
| POST | `/api/transactions` | Создать транзакцию |

**Пример создания транзакции:**
```json
POST /api/transactions
{
  "amount": 50000.00,
  "type": "INCOME",
  "categoryId": 1,
  "description": "Зарплата за январь"
}
```

### Бюджеты

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/budgets` | Получить все бюджеты |
| GET | `/api/budgets/category/{categoryId}` | Получить бюджет по категории |
| POST | `/api/budgets` | Создать/обновить бюджет |

**Пример установки бюджета:**
```json
POST /api/budgets
{
  "categoryId": 2,
  "limitAmount": 15000.00
}
```

### Переводы

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/transfers` | Все переводы (отправленные и полученные) |
| GET | `/api/transfers/sent` | Отправленные переводы |
| GET | `/api/transfers/received` | Полученные переводы |
| POST | `/api/transfers` | Создать перевод |

**Пример перевода:**
```json
POST /api/transfers
{
  "toUserLogin": "user2",
  "amount": 1000.00
}
```

### Статистика

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/statistics/summary` | Общая сводка (доходы, расходы, баланс) |
| GET | `/api/statistics/by-categories` | Суммы по категориям |
| GET | `/api/statistics/by-categories?categoryIds=1,2` | Суммы по выбранным категориям |
| GET | `/api/statistics/budget-status` | Статус бюджетов |

### Оповещения

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/notifications` | Получить все оповещения |

Типы оповещений:
- `BUDGET_EXCEEDED` — превышен лимит бюджета
- `EXPENSES_EXCEED_INCOME` — расходы превышают доходы

### Экспорт

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/export/summary` | Экспорт в JSON (по умолчанию) |
| GET | `/api/export/summary?format=text` | Экспорт в текстовый формат |

## Структура проекта

```
src/main/java/ru/mephi/ozerov/controlfinance/
├── ControlFinanceApplication.java    # Точка входа
├── config/                           # Конфигурация (Security, Jackson)
├── controller/                       # REST контроллеры
├── dto/                              # Data Transfer Objects
├── entity/                           # JPA сущности
├── exception/                        # Обработка исключений
├── repository/                       # JPA репозитории
└── service/                          # Бизнес-логика
    └── impl/                         # Реализации сервисов
```

## Модель данных

- **User** — пользователь (login, passwordHash)
- **Wallet** — кошелёк пользователя (balance)
- **Category** — категория (INCOME/EXPENSE)
- **Transaction** — транзакция (доход/расход)
- **Budget** — бюджет на категорию расходов
- **Transfer** — перевод между пользователями

## Авторизация

Все endpoints кроме `/api/auth/*` требуют аутентификации.

Используется HTTP Basic Authentication:
```bash
curl -u user1:password123 http://localhost:8080/api/wallet
```

## Валидация

Все входные данные валидируются. При ошибке возвращается ответ с кодом 400:

```json
{
  "timestamp": "2026-02-01T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/auth/register",
  "fieldErrors": [
    {"field": "login", "message": "Login must be between 3 and 50 characters"},
    {"field": "password", "message": "Password must be between 6 and 100 characters"}
  ]
}
```

## Анализ безопасности (SBOM)

Проект поддерживает генерацию SBOM (Software Bill of Materials) и анализ уязвимостей зависимостей.

### Генерация SBOM

SBOM генерируется с помощью CycloneDX Maven Plugin в формате JSON:

```bash
./mvnw cyclonedx:makeAggregateBom
```

Результат: `target/sbom.json`

### Структура SBOM файла

Файл содержит:
- Метаданные проекта (название, версия, описание)
- Список всех зависимостей с информацией:
  - groupId, artifactId, version
  - purl (Package URL) — унифицированный идентификатор пакета
  - scope (compile, runtime, test, provided)
  - хеши (MD5, SHA-1, SHA-256, SHA-512)
  - информация о лицензиях

### OWASP Dependency Check

Сканирование зависимостей на известные уязвимости (CVE):

```bash
# Требуется NVD API ключ для доступа к базе уязвимостей
export NVD_API_KEY=your-api-key
./mvnw dependency-check:check
```

Результат: `target/dependency-check/dependency-check-report.html`

Получить NVD API ключ: https://nvd.nist.gov/developers/request-an-api-key

### Dependency Track

Для загрузки SBOM в Dependency Track используется API:

```bash
curl -X POST "https://your-dtrack-server/api/v1/bom" \
  -H "X-Api-Key: YOUR_API_KEY" \
  -H "Content-Type: multipart/form-data" \
  -F "project=PROJECT_UUID" \
  -F "bom=@target/sbom.json"
```

### Сравнение инструментов

| Критерий | Dependency Check | Dependency Track |
|----------|------------------|------------------|
| Тип | CLI / Maven плагин | Веб-платформа |
| База данных | NVD (локальная копия) | NVD, GitHub Advisories, OSS Index |
| Отчёт | HTML, JSON, XML | Веб-интерфейс, API |
| Отслеживание | Разовый анализ | История изменений во времени |
| Интеграция | CI/CD скрипты | REST API, CI/CD |
| Уведомления | Нет | Email, Webhook, Slack |
| Лицензии | Не анализирует | Анализ лицензий |

Рекомендуется использовать оба инструмента:
- Dependency Check — быстрая проверка в CI pipeline
- Dependency Track — централизованный мониторинг всех проектов

## GitHub Actions CI/CD

Pipeline для автоматизации анализа безопасности находится в `.github/workflows/ci.yml`.

Триггер: создание или обновление pull request.

Джобы безопасности (запускаются только для PR):
- `generate-sbom` — генерация SBOM файла (CycloneDX)
- `dependency-check` — сканирование OWASP Dependency Check

Необходимые секреты в GitHub Settings → Secrets and variables → Actions:
- `NVD_API_KEY` — ключ для NVD API

### Локальный запуск Dependency Track (опционально)

```bash
# Запуск через Docker
docker run -d -p 8081:8080 --name dependency-track dependencytrack/bundled

# Открыть: http://localhost:8081
# Логин: admin / admin

# Загрузить SBOM вручную через UI или API:
curl -X POST "http://localhost:8081/api/v1/bom" \
  -H "X-Api-Key: YOUR_API_KEY" \
  -F "project=PROJECT_UUID" \
  -F "bom=@target/sbom.json"
```
