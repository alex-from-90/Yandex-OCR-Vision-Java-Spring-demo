package ru.alex.yandexocr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.yandexocr.service.ImageRecognitionService;

@Controller
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    private final ImageRecognitionService imageRecognitionService;

    // Конструктор для внедрения зависимостей
    public ImageUploadController(ImageRecognitionService imageRecognitionService) {
        this.imageRecognitionService = imageRecognitionService;
    }

    // Обработчик GET-запроса на главную страницу
    @GetMapping("/")
    public String index() {
        return "upload"; // Возвращает имя представления "upload" (Html страницу загрузки)
    }

    // Обработчик POST-запроса для распознавания изображения
    @PostMapping("/recognize")
    public String recognize(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Логирование начала распознавания изображения
            logger.info("Starting image recognition for file: {}", file.getOriginalFilename());

            // Вызов сервиса распознавания изображения и сохранение результата в модели
            String ocrResponse = imageRecognitionService.recognize(file);
            model.addAttribute("ocrResponse", ocrResponse);

            // Логирование успешного завершения распознавания
            logger.info("Image recognition completed successfully.");
        } catch (HttpClientErrorException.Unauthorized e) {
            // Логирование ошибки авторизации и добавление сообщения об ошибке в модель
            logger.error("Authorization error: Invalid token or access denied.", e);
            model.addAttribute("error", "Ошибка авторизации: неверный токен или отсутствует доступ.");
        } catch (Exception e) {
            // Логирование общей ошибки и добавление сообщения об ошибке в модель
            logger.error("Error during image recognition: {}", e.getMessage(), e);
            model.addAttribute("error", "Ошибка при распознавании изображения: " + e.getMessage());
        }

        return "upload"; // Возвращает имя представления "upload"
    }
}
