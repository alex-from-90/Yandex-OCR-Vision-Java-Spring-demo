package ru.alex.yandexocr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageRecognitionService {

    private final RestTemplateService restTemplateService;

    // Конструктор для инъекции зависимости RestTemplateService
    public ImageRecognitionService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    public String recognize(MultipartFile file) throws IOException {
        // Кодирование файла в Base64
        byte[] fileData = Base64.encodeBase64(file.getBytes());

        // Создание тела запроса для отправки на сервис распознавания
        String body = STR."""
{
  "mimeType": \"\{file.getContentType()}",
  "languageCodes": ["*"],
  "model": "page",
  "content": \"\{new String(fileData)}"
}""";

        // Отправка запроса и получение ответа от сервиса
        String responseBody = restTemplateService.postForEntity(body);

        // Инициализация ObjectMapper для работы с JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);

        // Получение блоков текста из ответа
        JsonNode blocks = jsonNode.path("result").path("textAnnotation").path("blocks");

        // Инициализация StringBuilder для формирования HTML-контента
        StringBuilder htmlContent = new StringBuilder();

        // Обход блоков, строк и слов для формирования HTML-контента
        for (JsonNode block : blocks) {
            for (JsonNode line : block.path("lines")) {
                for (JsonNode word : line.path("words")) {
                    // Получение текста и координат слова
                    String text = word.path("text").asText();
                    JsonNode boundingBox = word.path("boundingBox");
                    JsonNode topLeft = boundingBox.path("vertices").get(0);
                    int x = topLeft.path("x").asInt();
                    int y = topLeft.path("y").asInt();

                    // Создание HTML-элемента с абсолютным позиционированием
                    htmlContent.append(String.format("<div style='position: absolute; left: %dpx; top: %dpx;'>%s</div>", x, y, text));
                }
            }
        }

        // Возврат сформированного HTML-контента в виде строки
        return htmlContent.toString();
    }
}
