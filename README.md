# 🚀 Push Events — Distributed Transaction System

Микросервисная система обмена событиями через Apache Kafka с реализацией паттерна **SAGA** для распределённых транзакций.

---

## 📋 Содержание

- [Архитектура](#-архитектура)
- [Технологии](#-технологии)
- [Быстрый старт](#-быстрый-старт)
- [API Endpoints](#-api-endpoints)
- [Структура проекта](#-структура-проекта)

---

## 🏗 Архитектура

```
┌─────────────────┐     Kafka      ┌───────────────────┐
│                 │ ────────────▶  │                   │
│   Generator     │  event.created │    Registry       │
│    Service      │ ◀────────────  │     Service       │
│                 │ event.confirmed│                   │
└────────┬────────┘                └─────────┬─────────┘
         │                                   │
         ▼                                   ▼
   ┌──────────┐                        ┌──────────┐
   │ Postgres │                        │ Postgres │
   │ Generator│                        │ Registry │
   └──────────┘                        └──────────┘

┌─────────────────────────────────────────────────────┐
│                 SAGA Orchestrator                   │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐          │
│  │ Create  │──▶│ Reserve │──▶│ Process │          │
│  │ Order   │   │Inventory│   │ Payment │          │
│  └─────────┘   └─────────┘   └─────────┘          │
│       │             │             │                │
│       ▼             ▼             ▼                │
│   Compensation (откат) при сбое на любом шаге     │
└────────────────────────┬────────────────────────────┘
                         │
                         ▼
                   ┌──────────┐
                   │ Postgres │
                   │   Saga   │
                   └──────────┘
```

### Сервисы

| Сервис | Порт | Описание |
|--------|------|----------|
| **generator-service** | `8080` | Генерирует события по таймеру, отправляет в Kafka |
| **registry-service** | `8081` | Регистрирует события, отправляет подтверждения |
| **saga-orchestrator** | `8082` | Управляет распределёнными транзакциями (SAGA) |

---

## 🛠 Технологии

| Компонент | Версия |
|-----------|--------|
| Java | 21 |
| Spring Boot | 4.0.0-M1 |
| Apache Kafka | 7.5.0 (KRaft mode) |
| PostgreSQL | 16 |
| Spring Batch | ✓ |
| Docker & Docker Compose | ✓ |

---

## 🚀 Быстрый старт

### Требования

- Docker & Docker Compose
- ~4 GB свободной RAM

### Запуск

```bash
# 1. Клонируйте репозиторий
git clone <repository-url>
cd push-events

# 2. Соберите и запустите все сервисы
docker compose build
docker compose up -d

# 3. Проверьте статус
docker compose ps
```

### Остановка

```bash
docker compose down
```

### Пересборка отдельного сервиса

```bash
docker compose build <service-name> --no-cache
docker compose up -d <service-name>
```

---

## 📡 API Endpoints

### Generator Service — `http://localhost:8080`

#### Статистика событий

```bash
curl http://localhost:8080/stats
```

**Ответ:**
```json
{
  "total": 150,
  "processed": 142,
  "pending": 8
}
```

---

### Registry Service — `http://localhost:8081`

#### Список событий (с пагинацией)

```bash
curl "http://localhost:8081/events?page=0&size=10"
```

#### Фильтрация по типу события

```bash
curl "http://localhost:8081/events?eventType=ORDER_PLACED"
```

#### Фильтрация по источнику

```bash
curl "http://localhost:8081/events?sourceService=generator-service"
```

#### Фильтрация по датам

```bash
curl "http://localhost:8081/events?startDate=2026-01-20T00:00:00&endDate=2026-01-21T23:59:59"
```

#### Комбинированный запрос

```bash
curl "http://localhost:8081/events?eventType=USER_CREATED&page=0&size=5&startDate=2026-01-01T00:00:00"
```

---

### SAGA Orchestrator — `http://localhost:8082`

#### Запустить новую SAGA транзакцию

```bash
curl -X POST http://localhost:8082/saga/start
```

**Ответ:**
```json
{
  "id": 1,
  "status": "COMPLETED",
  "startedAt": "2026-01-20T12:00:00",
  "completedAt": "2026-01-20T12:00:01",
  "currentStep": 3,
  "steps": [...]
}
```

#### Статистика транзакций

```bash
curl http://localhost:8082/saga/stats
```

**Ответ:**
```json
{
  "total": 100,
  "completed": 85,
  "failed": 10,
  "compensated": 5
}
```

#### Список транзакций (с пагинацией)

```bash
curl "http://localhost:8082/saga?page=0&size=10"
```

#### Фильтрация по статусу

```bash
curl "http://localhost:8082/saga?status=COMPLETED"
curl "http://localhost:8082/saga?status=COMPENSATED"
```

#### Фильтрация по датам

```bash
curl "http://localhost:8082/saga?startDate=2026-01-20T00:00:00&endDate=2026-01-20T23:59:59"
```

#### Получить транзакцию по ID

```bash
curl http://localhost:8082/saga/1
```

---

## 📁 Структура проекта

```
push-events/
├── docker-compose.yml          # Конфигурация Docker Compose
├── Dockerfile                  # Общий Dockerfile для сервисов
├── pom.xml                     # Родительский POM
│
├── push-events-api/            # Общие модели (Kafka messages)
│   └── src/main/java/.../model/
│       ├── EventCreatedMessage.java
│       ├── EventConfirmedMessage.java
│       └── EventType.java
│
├── generator-service/          # Сервис-генератор событий
│   └── src/main/java/.../
│       ├── controller/         # REST API
│       ├── service/            # Бизнес-логика
│       ├── repository/         # JPA репозитории
│       ├── model/              # JPA сущности
│       └── task/               # Scheduled задачи
│
├── registry-service/           # Сервис-регистратор событий
│   └── src/main/java/.../
│       ├── controller/         # REST API с фильтрацией
│       ├── service/            # Бизнес-логика
│       ├── batch/              # Spring Batch конфигурация
│       └── repository/         # JPA с Specifications
│
└── saga-orchestrator/          # SAGA оркестратор
    └── src/main/java/.../
        ├── controller/         # REST API
        ├── service/            # Оркестрация транзакций
        ├── step/               # Обработчики шагов SAGA
        ├── model/              # Сущности и Enums
        └── repository/         # JPA репозитории
```

---

## ⚙️ Конфигурация

### Вероятность сбоя в SAGA

В `saga-orchestrator/src/main/resources/application.yaml`:

```yaml
saga:
  failure:
    probability: 0.3  # 30% шанс сбоя на каждом шаге
```

### Интервал генерации событий

В `generator-service/src/main/resources/application.yaml`:

```yaml
event:
  scheduler:
    interval: 5000  # каждые 5 секунд
```

---

## 🗄 Базы данных

| База | Порт | Credentials |
|------|------|-------------|
| postgres-generator | `5433` | user / pass |
| postgres-registry | `5434` | user / pass |
| postgres-saga | `5435` | user / pass |

### Подключение к БД

```bash
# Generator DB
psql -h localhost -p 5433 -U user -d generator

# Registry DB
psql -h localhost -p 5434 -U user -d registry

# Saga DB
psql -h localhost -p 5435 -U user -d saga
```

---

## 📝 Логирование

Все сервисы выводят логи в stdout. Просмотр логов:

```bash
# Все сервисы
docker compose logs -f

# Конкретный сервис
docker compose logs -f generator-service
docker compose logs -f registry-service
docker compose logs -f saga-orchestrator
```

---

## 🧪 Тестирование SAGA

```bash
# Запустить 10 SAGA транзакций
for i in {1..10}; do curl -X POST http://localhost:8082/saga/start; echo; done

# Проверить статистику
curl http://localhost:8082/saga/stats
```

**PowerShell:**

```powershell
# Запустить 10 SAGA транзакций
1..10 | ForEach-Object { Invoke-RestMethod -Method POST -Uri http://localhost:8082/saga/start }

# Проверить статистику
Invoke-RestMethod -Uri http://localhost:8082/saga/stats
```

---
