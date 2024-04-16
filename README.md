# Руководство по использованию Yandex OCR с Spring Boot

Этот проект демонстрирует пример использования Yandex OCR (Optical Character Recognition) для распознавания текста на изображениях с помощью Spring Boot.

## Начало работы

Для начала работы вам необходимо получить API ключ для Yandex Vision. 
Подробная инструкция по получению ключа и описание приложения доступно на канале [My_It_World](https://t.me/my_it_word ) в Telegram.

## Настройка API ключа
Откройте файл application.properties и добавьте ваш API ключ и ID папки Yandex cloud:

**YANDEX_OCR_TOKEN= ваш API ключ** <br />
**YANDEX_OCR_FOLDER_ID= id папки**

## Запуск и использование
После запуска приложения вы сможете открыть веб-браузер и перейти по адресу http://localhost:5050 для загрузки изображения и его распознавания.

### Зависимости
* Spring Boot
* Jackson
* Commons Codec
* SLF4J
* Thymeleaf
* Spring Web