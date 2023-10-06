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
   а) Пользователь отправляет GET запрос вида:  
   ``
   curl --location --request GET 'http://localhost:8080/oauth2/authorize?response_type=code&client_id=url-shortcut-client&redirect_uri=http://localhost:8080/code'  
   ``
   вводит login и password и получает код авторизации для получения JWT-токена.  
   б) Далее выполняем запрос вида:  
   ``
   curl --location --request POST 'localhost:8080/oauth2/token?grant_type=authorization_code&code=$code&redirect_uri=http://localhost:8080/code' \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --header 'Authorization: Basic $basic'
   ``  
   вместо $code нужно подставить полученный код;  
   вместо $basic нужно подставить креды клиента вида url-shortcut-client:secret закодированные в base64 (по дефолтным
   настройкам это dXJsLXNob3J0Y3V0LWNsaWVudDpzZWNyZXQ=);

   В ответ придет JWT ACCESS токен. Этот токен в дальнейшем отправляем в заголовке AUTHORISATION

4. Регистрация URL. Поле того, как пользователь зарегистрировал свой сайт он может отправлять на сайт ссылки и получать
   преобразованные ссылки.  
   Отправляем запрос ``POST /convert`` c телом JSON объекта ``{url : "http://mysite.ru/123"}``  
   В ответ получаем ``{code : "ZRUfdD2"}`` - ключ ZRUfD2 ассоциирован с URL.

5. Переадресация.  
   Выполняется без авторизации. Отправляем запрос ``GET /redirect/УНИКАЛЬНЫЙ_КОД``. Ответ от сервера в
   заголовке ``HTTP CODE - 302 REDIRECT URL``

6. Статистика.  
   В сервисе считается количество вызовов каждого адреса.  
   По сайту можно получить статистку всех адресов и количество вызовов этого адреса; Для получения статистики
   отправляем ``GET /statistic``  
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


