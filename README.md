
# Client-server app
*Client-server application on Java*

## Information
* Implemented a console application that manages data interactively.
* The application is divided into client and server parts.
* Implemented data conversion from JSON and validation on the client with sending ready-made instances to the server and to database (PostgreSQL) for execution.
* User system implemented.
* Programming patterns are implemented (Singleton, Iterator, Commands, Decorator).

## Requirements
* Python 3.6+
* Gradle 7.1+
* PostgreSQL 14+

## Task
1. Организовать хранение коллекции в реляционной СУБД (PostgresQL). Убрать хранение коллекции в файле.
2. Для генерации поля id использовать средства базы данных (sequence).
3. Обновлять состояние коллекции в памяти только при успешном добавлении объекта в БД
4. Все команды получения данных должны работать с коллекцией в памяти, а не в БД
5. Организовать возможность регистрации и авторизации пользователей. У пользователя есть возможность указать пароль.
6. Пароли при хранении хэшировать алгоритмом SHA-224
7. Запретить выполнение команд не авторизованным пользователям.
8. При хранении объектов сохранять информацию о пользователе, который создал этот объект.
9. Пользователи должны иметь возможность просмотра всех объектов коллекции, но модифицировать могут только принадлежащие им.
10. Для идентификации пользователя отправлять логин и пароль с каждым запросом.

### Необходимо реализовать многопоточную обработку запросов 
1. Для многопоточного чтения запросов использовать Fixed thread pool
2. Для многопотчной обработки полученного запроса использовать ForkJoinPool
3. Для многопоточной отправки ответа использовать Cached thread pool
4. Для синхронизации доступа к коллекции использовать синхронизацию чтения и записи с помощью java.util.concurrent.locks.ReadWriteLock
