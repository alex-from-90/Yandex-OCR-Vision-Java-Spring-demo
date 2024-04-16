package ru.alex.yandexocr.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestTemplateService {

    // Инъекция значений из файла application.properties
    @Value("${YANDEX_OCR_TOKEN}")
    private String token;
    @Value("${YANDEX_OCR_FOLDER_ID}")
    private String folderId;
    @Value("${ocr.api.url}")
    private String url;

    // Метод для отправки POST-запроса на указанный URL с заданным телом запроса
    public String postForEntity(String body) {
        // Установка заголовков запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Установка авторизационного токена и идентификатора папки в заголовки запроса
        headers.set("Authorization", STR."Api-Key \{token}");
        headers.set("x-folder-id", STR."\{folderId}");
        headers.set("x-data-logging-enabled", "true");

        // Создание HTTP-сущности с телом и заголовками
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Инициализация RestTemplate для выполнения HTTP-запроса
        RestTemplate restTemplate = new RestTemplate();

        // Выполнение POST-запроса и получение ответа
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // Возврат тела ответа в виде строки
        return response.getBody();
    }
}
