# Document Service

## Описание

Это тестовый проект, который представляет собой сервис управления документами,
позволяет создавать, обновлять и проверять статус документов.

## Требования

- Java 21
- Maven
- PostgreSQL
- Doker - compose

## Запуск

1. Запустить основной сервиса.
Для запуска сервиса нужно:
 - запустить compose.yaml
 - запустить itqGroupTestApp-core/src/main/java/itqGroupTestApp/core/ItqGroupTestAppApplication.java

Результат - выкачка и старт контейнера с PostgreSQL. 
Будут накачены 5 миграций по созданию 4 таблиц и инсерту тестового юзера 

2. Запустить сервис по созданию пачки документов 
перед запуском:
Установить размер пачки в файле 
itqGroupTestApp-auto-create-docs/src/main/resources/application.yaml
При необходимости изменить время между запусками демон воркеров - 
itqGroupTestApp-core/src/main/java/itqGroupTestApp/core/servises/BackgroundService.java
Размер пачки можно утвновить в  файле
itqGroupTestApp-core/src/main/resources/application.yaml

Для запуска сервиса нужно:

- запустить itqGroupTestApp-auto-create-docs/src/main/java/itqGroupTestApp/auto/create/docs/DocumentGenerator.java
 
Результат- создание пачки документов в бд.

3. Логи.
 - пример создания пачки документов :
 Starting document generation for 10 documents
 Successfully created document: Document 1
 Progress: 1 of 10 documents created
 Successfully created document: Document 2
 Progress: 2 of 10 documents created

- пример апрува пачки документов(сабмит аналогично) :
 Starting approve documents for 3 documents
 Progress: 1 of 3 documents approved
 Progress: 2 of 3 documents approved
 Progress: 3 of 3 documents approved
 Approve documents ended, duration: 26 ms

- пример лога ошибки:
  Error submitting document with ID: null: The given id must not be null

4. Пояснения некоторые:
 - период при поиске доков - по дате апдейта

5. Ответы:
 -запрос с 5000+ id :
   - обработка через параллельный стрим 
   - асинхранная обработка запроса с сохранением а базу пачками 
   - при выборке держать кеш с результатами (считаем что сохранение не частое но большими пачками)
   - раскатить на несколько инстансов с шардом базы с апи гейтвеем.

 -вынос реестра в отдельную систему:
   - обдельная база (схема)
   - рест-апи с передачей айди операции в хедерах на взаимодействие с отправкой батчами
   - реализация отката изменений (помним про хедеры). 
   - ретраи на провальныее результаты апрува.
   - метрики (прометеус) на оба сервиса
