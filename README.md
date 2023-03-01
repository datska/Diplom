# Дипломный проект профессии "Тестировщик"

## Документация по проекту
1. [План тестирования](https://github.com/datska/Diplom/blob/master/docs/Plan.md)
2. [Отчет по итогам тестирования](https://github.com/datska/Diplom/blob/master/docs/Report.md)
3. [Отчет по итогам автоматизации](https://github.com/datska/Diplom/blob/master/docs/Report.md)

## Запуск приложения


* склонировать репозиторий `https://github.com/datska/Diplom.git` на локальную машину командой `git clone`

* открыть проект в IDEA

* запустить контейнеры в Docker Compose `docker compose up`
* в терминале IntelliJ IDEA запустить SUT:

    - с использованием БД MySQL командой `java "-Dspring.datasource.url=jdbc:mysql://localhost:3300/app" -jar artifacts/aqa-shop.jar`
    - с использованием БД PostgreSQL командой `java -jar artifacts/aqa-shop.jar "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app"`

* запустить автотесты командой:

    - для конфигурации БД MySql:
      `./gradlew clean test "-Ddatasource.url=jdbc:mysql://localhost:3300/app"`
    - для конфигурации БД PostgreSQL:
      `./gradlew clean test "-Ddatasource.url=jdbc:postgresql://localhost:5432/app"`

* запустить отчеты командой:

    - `./gradlew allureReport` (первоначальная команда)

    - `./gradlew allureServe` (запуск и открытие отчетов)

* остановить SUT комбдинацией клавиш `CTRL+C`

* остановить контейнеры командой `docker compose stop`, удалить контейнеры командой `docker compose down`
