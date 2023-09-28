# job4j_url-shortcut

[![build](https://github.com/SergeyPoletaev/job4j_url_shortcut/workflows/build/badge.svg)](https://github.com/SergeyPoletaev/job4j_url_shortcut/actions)

### Описание проекта:

REST API сервис по замене url адресов на короткие обезличенные ярлыки.  
Пользоваться сервисом могут только зарегистрированные пользователи.

1. Pегистрация.  
   Для регистрации отправляем запрос
   ``POST /registration`` c телом JSON объекта ``{site : "http://mysite.ru"}``  
   Ответ от сервера будет содержать
   JSON ``{registration : true/false, login: УНИКАЛЬНЫЙ_КОД, password : УНИКАЛЬНЫЙ_КОД}``  
   Флаг registration указывает, что регистрация выполнена или нет, то есть сайт уже есть в системе.

2. Авторизация.  
   Пользователь отправляет POST запрос с login и password и получает JWT-токен. Этот токен в дальнейшем отправляет в
   заголовке AUTHORISATION

3. Регистрация URL. Поле того, как пользователь зарегистрировал свой сайт он может отправлять на сайт ссылки и получать
   преобразованные ссылки.  
   Отправляем запрос ``POST /convert`` c телом JSON объекта ``{url : "http://mysite.ru/123"}``  
   В ответ получаем ``{code : "ZRUfdD2"}`` - ключ ZRUfD2 ассоциирован с URL.

4. Переадресация.  
   Выполняется без авторизации. Отправляем запрос ``GET /redirect/УНИКАЛЬНЫЙ_КОД``. Ответ от сервера в
   заголовке ``HTTP CODE - 302 REDIRECT URL``

5. Статистика.  
   В сервисе считается количество вызовов каждого адреса.  
   По сайту можно получить статистку:
   - всех адресов (для пользователя с правами ADMIN) и количество вызовов этого адреса;
   - адресов, которые зарегистрировал конкретный пользователь (для пользователя с правами USER) и количество вызовов
     этого адреса.  
     Для получения статистики отправляем ``GET /statistic``  
     Ответ
     сервера ``{ {url : URL, total : 0}, {url : "https://mysite.ru/profile/exercise/106/task-view/532", total : 103} }``

### Стек технологий:

* Java 17
* Maven 3.8
* Spring Boot 2
* Spring Security
* PostgreSQL 14
* Liquibase

### Требования к окружению:

* Java 17
* Maven 3.8
* PostgreSQL 14

### Запуск проекта:

1. Создать базу данных, например через утилиту psql:

``` 
create database job4j_urlshortcut 
```

2. Упаковать проект в jar архив. Для этого в папке с проектом выполнить:

``` 
mvn package   
```  

Архив jar будет находится по пути: ./job4j_url_shortcut/target/job4j_url_shortcut-1.0.jar

4. Добавить переменные на подключения к базе данных:
   ```
   export DB_DEFAULT_URL=...
   export DB_DEFAULT_USERNAME=...
   export DB_DEFAULT_PASSWORD=...
   ```
5. Запустить приложение командой:

``` 
java -jar job4j_url_shortcut-1.0.jar 
```

---


