# Todo API

Учебный REST API для личных задач на Spring Boot, Spring Security, JWT и PostgreSQL.

## Возможности

- регистрация и вход по JWT;
- задачи доступны только их владельцу;
- создание, просмотр, изменение и удаление задач;
- статусы `PENDING`, `IN_PROGRESS`, `COMPLETED`;
- приоритеты `LOW`, `MEDIUM`, `HIGH`;
- необязательный дедлайн;
- поиск, фильтрация, сортировка и пагинация;
- единый JSON-формат ошибок;
- интеграционные тесты на H2, не затрагивающие PostgreSQL.

## Локальный запуск

Нужны Java 17+ и работающий PostgreSQL с базой `todo_db`.

Настрой переменные окружения:

```text
DB_PASSWORD=пароль_от_postgres
JWT_SECRET=секрет_в_Base64
```

Для JWT нужен случайный ключ не короче 32 байт. В PowerShell его можно получить так:

```powershell
$bytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

После настройки переменных запусти приложение из IntelliJ IDEA либо командой:

```powershell
.\mvnw.cmd spring-boot:run
```

## Запуск через Docker

Скопируй `.env.example` в `.env`, задай реальные `DB_PASSWORD` и `JWT_SECRET`, затем выполни:

```powershell
docker compose up --build
```

## Основные endpoints

| Метод | URL | Назначение |
|---|---|---|
| `POST` | `/auth/register` | Регистрация |
| `POST` | `/auth/login` | Получение JWT |
| `POST` | `/tasks` | Создание задачи |
| `GET` | `/tasks` | Все задачи текущего пользователя |
| `GET` | `/tasks/{id}` | Одна задача текущего пользователя |
| `PUT` | `/tasks/{id}` | Изменение задачи |
| `PATCH` | `/tasks/{id}/status` | Изменение статуса |
| `DELETE` | `/tasks/{id}` | Удаление задачи |
| `GET` | `/tasks/status/{status}` | Фильтр по статусу |
| `GET` | `/tasks/search` | Поиск, фильтры и пагинация |

Кроме `/auth/**`, запросы требуют заголовок:

```text
Authorization: Bearer <token>
```

### Создание задачи

```json
{
  "title": "Finish Todo API",
  "description": "Add tests and documentation",
  "priority": "HIGH",
  "dueDate": "2026-12-01T18:00:00"
}
```

Если `priority` не передан, используется `MEDIUM`. `dueDate` можно не передавать.

### Изменение статуса

```json
{
  "status": "COMPLETED"
}
```

### Поиск

Пример:

```text
GET /tasks/search?query=spring&status=PENDING&priority=HIGH&page=0&size=20&sortBy=dueDate&direction=asc
```

Разрешённые поля сортировки: `createdAt`, `dueDate`, `title`, `status`, `priority`.

## Тесты

```powershell
.\mvnw.cmd test
```

Тесты используют отдельную базу H2 в памяти и автоматически проверяют, что один пользователь не может читать или удалять задачи другого.
